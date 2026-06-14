package mx.uv.sicae.users.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import mx.uv.sicae.users.dto.CambiarEstatusRequest;
import mx.uv.sicae.users.dto.EditarUsuarioRequest;
import mx.uv.sicae.users.dto.RegistrarUsuarioRequest;
import mx.uv.sicae.users.dto.UsuarioResponse;
import mx.uv.sicae.users.model.UsuarioEntity;
import mx.uv.sicae.users.model.UsuarioPerfil;
import mx.uv.sicae.users.repository.CatalogoRepository;
import mx.uv.sicae.users.repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private static final int ID_ROL_ADMINISTRADOR = 1;
    private static final int MAX_NOMBRE = 50;
    private static final int MAX_APELLIDO = 50;
    private static final int MAX_USERNAME = 30;
    private static final int MAX_EMAIL = 255;
    private static final int MAX_TELEFONO = 10;
    private static final int MAX_PASSWORD_BCRYPT = 72;
    private static final int INTENTOS_CLAVE_USUARIO = 10;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE);

    private final UsuarioRepository usuarioRepository;
    private final CatalogoRepository catalogoRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, CatalogoRepository catalogoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.catalogoRepository = catalogoRepository;
    }

    public List<UsuarioResponse> listarUsuarios() {
        List<UsuarioPerfil> perfiles = usuarioRepository.listarTodos();
        log.debug("Usuarios encontrados: {}", perfiles.size());
        return perfiles.stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    public UsuarioResponse obtenerPerfil(Integer idUsuario) {
        validarIdUsuario(idUsuario);
        UsuarioPerfil perfil = usuarioRepository.buscarPerfilPorId(idUsuario);
        if (perfil == null) {
            throw new IllegalArgumentException("No se encontro el usuario con id " + idUsuario);
        }
        return UsuarioResponse.fromEntity(perfil);
    }

    @Transactional
    public UsuarioResponse crearUsuario(RegistrarUsuarioRequest request, Integer idRolAutenticado) {
        validarAdministrador(idRolAutenticado);
        DatosRegistro datos = validarDatosRegistro(request);
        validarCatalogos(datos.idRol(), datos.idTipoUsuario(), datos.idProgramaEducativo());
        validarUsernameDisponible(datos.username());
        validarEmailDisponible(datos.email(), null);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNombre(datos.nombre());
        usuario.setApellidoPaterno(datos.apellidoPaterno());
        usuario.setApellidoMaterno(datos.apellidoMaterno());
        usuario.setEmail(datos.email());
        usuario.setTelefono(datos.telefono());
        usuario.setUsername(datos.username());
        usuario.setPassword(BCrypt.hashpw(datos.password(), BCrypt.gensalt()));
        usuario.setClaveUsuario(generarClaveUsuario());
        usuario.setEstatus(true);
        usuario.setIdRol(datos.idRol());
        usuario.setIdTipoUsuario(datos.idTipoUsuario());
        usuario.setIdProgramaEducativo(datos.idProgramaEducativo());
        usuario.setTiempoCreacion(LocalDateTime.now());
        usuario.setTempoActualizacion(LocalDateTime.now());

        usuarioRepository.insertar(usuario);

        log.info("Usuario creado: idUsuario={}, username={}, claveUsuario={}",
                usuario.getIdUsuario(), datos.username(), usuario.getClaveUsuario());

        return obtenerPerfilRegistrado(usuario.getIdUsuario(), datos.username());
    }

    @Transactional
    public UsuarioResponse editarUsuario(Integer idUsuario, EditarUsuarioRequest request,
                                          Integer idUsuarioAutenticado, Integer idRolAutenticado) {
        validarIdUsuario(idUsuario);
        validarPropietarioOAdministrador(idUsuario, idUsuarioAutenticado, idRolAutenticado);
        if (request == null) {
            throw new IllegalArgumentException("Los datos del usuario son obligatorios");
        }
        obtenerPerfil(idUsuario);
        validarCamposNoEditables(request);

        DatosEdicion datos = validarDatosEdicion(request);
        validarCatalogos(datos.idRol(), datos.idTipoUsuario(), datos.idProgramaEducativo());
        validarEmailDisponible(datos.email(), idUsuario);

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre(datos.nombre());
        usuario.setApellidoPaterno(datos.apellidoPaterno());
        usuario.setApellidoMaterno(datos.apellidoMaterno());
        usuario.setEmail(datos.email());
        usuario.setTelefono(datos.telefono());
        usuario.setIdRol(datos.idRol());
        usuario.setIdTipoUsuario(datos.idTipoUsuario());
        usuario.setIdProgramaEducativo(datos.idProgramaEducativo());
        usuario.setTempoActualizacion(LocalDateTime.now());

        int filas = usuarioRepository.actualizar(usuario);
        if (filas == 0) {
            throw new IllegalStateException("No se pudo actualizar el usuario");
        }

        log.info("Usuario editado: idUsuario={}", idUsuario);

        return obtenerPerfil(idUsuario);
    }

    @Transactional
    public UsuarioResponse cambiarEstatus(Integer idUsuario,
                                          CambiarEstatusRequest request,
                                          Integer idUsuarioAutenticado,
                                          Integer idRolAutenticado) {
        validarAdministrador(idRolAutenticado);
        validarIdUsuario(idUsuario);
        if (request == null) {
            throw new IllegalArgumentException("Los datos del estatus son obligatorios");
        }
        if (request.getIdUsuario() != null && !request.getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("El idUsuario del cuerpo no coincide con la ruta");
        }
        if (request.getEstatus() == null) {
            throw new IllegalArgumentException("estatus es obligatorio");
        }
        if (idUsuarioAutenticado != null && idUsuarioAutenticado.equals(idUsuario)) {
            throw new IllegalArgumentException("No se puede cambiar el estatus del propio usuario autenticado");
        }

        obtenerPerfil(idUsuario);

        int filas = usuarioRepository.cambiarEstatus(idUsuario, request.getEstatus());
        if (filas == 0) {
            throw new IllegalStateException("No se pudo cambiar el estatus del usuario");
        }

        log.info("Estatus cambiado: idUsuario={}, nuevoEstatus={}", idUsuario, request.getEstatus());

        return obtenerPerfil(idUsuario);
    }

    private void validarPropietarioOAdministrador(Integer idUsuario, Integer idUsuarioAutenticado, Integer idRolAutenticado) {
        if (idRolAutenticado != null && idRolAutenticado == ID_ROL_ADMINISTRADOR) {
            return;
        }
        if (idUsuarioAutenticado != null && idUsuarioAutenticado.equals(idUsuario)) {
            return;
        }
        throw new SecurityException("No tienes permiso para editar este usuario");
    }

    private DatosRegistro validarDatosRegistro(RegistrarUsuarioRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos del usuario son obligatorios");
        }

        return new DatosRegistro(
                validarIdCatalogo(request.getIdRol(), "idRol"),
                validarIdCatalogo(request.getIdTipoUsuario(), "idTipoUsuario"),
                validarIdCatalogo(request.getIdProgramaEducativo(), "idProgramaEducativo"),
                validarTextoObligatorio(request.getNombre(), "nombre", MAX_NOMBRE),
                validarTextoObligatorio(request.getApellidoPaterno(), "apellidoPaterno", MAX_APELLIDO),
                validarTextoOpcional(request.getApellidoMaterno(), "apellidoMaterno", MAX_APELLIDO),
                validarUsername(request.getUsername()),
                validarPassword(request.getPassword()),
                validarEmail(request.getEmail()),
                validarTextoObligatorio(request.getTelefono(), "telefono", MAX_TELEFONO));
    }

    private DatosEdicion validarDatosEdicion(EditarUsuarioRequest request) {
        return new DatosEdicion(
                validarIdCatalogo(request.getIdRol(), "idRol"),
                validarIdCatalogo(request.getIdTipoUsuario(), "idTipoUsuario"),
                validarIdCatalogo(request.getIdProgramaEducativo(), "idProgramaEducativo"),
                validarTextoObligatorio(request.getNombre(), "nombre", MAX_NOMBRE),
                validarTextoObligatorio(request.getApellidoPaterno(), "apellidoPaterno", MAX_APELLIDO),
                validarTextoOpcional(request.getApellidoMaterno(), "apellidoMaterno", MAX_APELLIDO),
                validarEmail(request.getEmail()),
                validarTextoObligatorio(request.getTelefono(), "telefono", MAX_TELEFONO));
    }

    private void validarCamposNoEditables(EditarUsuarioRequest request) {
        if (tieneTexto(request.getUsername())) {
            throw new IllegalArgumentException("No se puede editar directamente el usuario");
        }
        if (tieneTexto(request.getPassword())) {
            throw new IllegalArgumentException("No se puede editar directamente la contrasena");
        }
        if (tieneTexto(request.getClaveUsuario())) {
            throw new IllegalArgumentException("No se puede editar directamente la clave del usuario");
        }
    }

    private void validarAdministrador(Integer idRolAutenticado) {
        if (idRolAutenticado == null) {
            throw new SecurityException("El token JWT no contiene el rol del usuario autenticado");
        }
        if (idRolAutenticado != ID_ROL_ADMINISTRADOR) {
            throw new SecurityException("Solo usuarios con rol de administrador pueden realizar esta operacion");
        }
    }

    private void validarCatalogos(Integer idRol, Integer idTipoUsuario, Integer idProgramaEducativo) {
        if (catalogoRepository.contarRolActivoPorId(idRol) == 0) {
            throw new IllegalArgumentException("El rol indicado no existe o no esta activo");
        }
        if (catalogoRepository.contarTipoUsuarioActivoPorId(idTipoUsuario) == 0) {
            throw new IllegalArgumentException("El tipo de usuario indicado no existe o no esta activo");
        }
        if (catalogoRepository.contarProgramaEducativoActivoPorId(idProgramaEducativo) == 0) {
            throw new IllegalArgumentException("El programa educativo indicado no existe o no esta activo");
        }
    }

    private void validarUsernameDisponible(String username) {
        if (usuarioRepository.buscarPorUsername(username) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con el username indicado");
        }
    }

    private void validarEmailDisponible(String email, Integer idUsuarioActual) {
        UsuarioPerfil usuario = usuarioRepository.buscarPorEmail(email);
        if (usuario != null && (idUsuarioActual == null || !usuario.getIdUsuario().equals(idUsuarioActual))) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo indicado");
        }
    }

    private String generarClaveUsuario() {
        for (int intento = 0; intento < INTENTOS_CLAVE_USUARIO; intento++) {
            String clave = "RGR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            if (usuarioRepository.buscarPorClaveUsuario(clave) == null) {
                return clave;
            }
        }
        throw new IllegalStateException("No se pudo generar una clave de usuario unica");
    }

    private UsuarioResponse obtenerPerfilRegistrado(Integer idUsuario, String username) {
        UsuarioPerfil perfil = null;
        if (idUsuario != null) {
            perfil = usuarioRepository.buscarPerfilPorId(idUsuario);
        }
        if (perfil == null) {
            perfil = usuarioRepository.buscarPorUsername(username);
        }
        if (perfil == null) {
            throw new IllegalStateException("No se pudo recuperar el usuario registrado");
        }
        return UsuarioResponse.fromEntity(perfil);
    }

    private void validarIdUsuario(Integer idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("idUsuario es obligatorio");
        }
    }

    private Integer validarIdCatalogo(Integer valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser mayor a cero");
        }
        return valor;
    }

    private String validarUsername(String valor) {
        return validarTextoObligatorio(valor, "usuario", MAX_USERNAME).toLowerCase();
    }

    private String validarPassword(String valor) {
        return validarTextoObligatorio(valor, "contrasena", MAX_PASSWORD_BCRYPT);
    }

    private String validarEmail(String valor) {
        String email = validarTextoObligatorio(valor, "correo", MAX_EMAIL).toLowerCase();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("correo debe tener un formato valido");
        }
        return email;
    }

    private String validarTextoObligatorio(String valor, String campo, int maximo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
        String normalizado = valor.trim();
        if (normalizado.length() > maximo) {
            throw new IllegalArgumentException(campo + " no debe exceder " + maximo + " caracteres");
        }
        return normalizado;
    }

    private String validarTextoOpcional(String valor, String campo, int maximo) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        String normalizado = valor.trim();
        if (normalizado.length() > maximo) {
            throw new IllegalArgumentException(campo + " no debe exceder " + maximo + " caracteres");
        }
        return normalizado;
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }

    private record DatosRegistro(Integer idRol,
                                 Integer idTipoUsuario,
                                 Integer idProgramaEducativo,
                                 String nombre,
                                 String apellidoPaterno,
                                 String apellidoMaterno,
                                 String username,
                                 String password,
                                 String email,
                                 String telefono) {
    }

    private record DatosEdicion(Integer idRol,
                                Integer idTipoUsuario,
                                Integer idProgramaEducativo,
                                String nombre,
                                String apellidoPaterno,
                                String apellidoMaterno,
                                String email,
                                String telefono) {
    }
}
