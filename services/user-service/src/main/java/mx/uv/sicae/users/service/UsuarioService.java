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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

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

    // Devuelve todos los usuarios. Solo el admin puede hacer esto.
    public List<UsuarioResponse> listarUsuarios(Integer idRolAutenticado) {
        validarAdministrador(idRolAutenticado);
        List<UsuarioPerfil> perfiles = usuarioRepository.listarTodos();
        return perfiles.stream()
                .map(UsuarioResponse::fromEntity)
                .toList();
    }

    // Obtiene la info de un usuario por su id. Lanza error si no existe.
    public UsuarioResponse obtenerPerfil(Integer idUsuario) {
        validarIdUsuario(idUsuario);
        UsuarioPerfil perfil = usuarioRepository.buscarPerfilPorId(idUsuario);
        if (perfil == null) {
            throw new IllegalArgumentException("No se encontro el usuario con id " + idUsuario);
        }
        return UsuarioResponse.fromEntity(perfil);
    }

    // Crea un usuario nuevo. Valida datos, catalogo, username/email unicos,
    // encripta la contrasena y genera la clave. Solo admin.
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

        return obtenerPerfilRegistrado(usuario.getIdUsuario(), datos.username());
    }
    // Edita los datos de un usuario. No deja cambiar username, password ni claveUsuario.
    // Puede hacerlo el admin o el propio usuario.
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

        return obtenerPerfil(idUsuario);
    }
    // Activa o desactiva un usuario. Solo admin, y no puede hacerse a si mismo.
    @Transactional
    public UsuarioResponse cambiarEstatus(Integer idUsuario, CambiarEstatusRequest request,
                                           Integer idUsuarioAutenticado, Integer idRolToken) {
        validarIdUsuario(idUsuario);
        if (request == null) {
            throw new IllegalArgumentException("Los datos del estatus son obligatorios");
        }
        if (request.getIdUsuario() != null && !request.getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("El idUsuario del cuerpo no coincide con la ruta");
        }
        if (request.getIdRol() == null) {
            throw new IllegalArgumentException("idRol es obligatorio");
        }
        if (!request.getIdRol().equals(idRolToken)) {
            throw new IllegalArgumentException("El idRol del cuerpo no coincide con el token de autenticacion");
        }
        validarAdministrador(request.getIdRol());
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

        return obtenerPerfil(idUsuario);
    }

    // Revisa que quien hace la peticion sea admin o el dueno del recurso.
    private void validarPropietarioOAdministrador(Integer idUsuario, Integer idUsuarioAutenticado, Integer idRolAutenticado) {
        if (idRolAutenticado != null && idRolAutenticado == ID_ROL_ADMINISTRADOR) {
            return;
        }
        if (idUsuarioAutenticado != null && idUsuarioAutenticado.equals(idUsuario)) {
            return;
        }
        throw new SecurityException("No tienes permiso para editar este usuario");
    }

    // Valida y normaliza todos los campos del formulario de registro.
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

    // Valida y normaliza los campos del formulario de edicion.
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

    // Rechaza la edicion si el usuario intento cambiar username, password o claveUsuario.
    private void validarCamposNoEditables(EditarUsuarioRequest request) {
        if (tieneTexto(request.getUsername())) {
            throw new IllegalArgumentException("No se puede editar directamente el usuario");
        }
        if (tieneTexto(request.getPassword())) {
            throw new IllegalArgumentException("No se puede editar directamente la contraseña");
        }
        if (tieneTexto(request.getClaveUsuario())) {
            throw new IllegalArgumentException("No se puede editar directamente la clave del usuario");
        }
    }

    // Se asegura de que el usuario tenga rol de administrador (id=1).
    private void validarAdministrador(Integer idRolAutenticado) {
        if (idRolAutenticado == null) {
            throw new SecurityException("El token JWT no contiene el rol del usuario autenticado");
        }
        if (idRolAutenticado != ID_ROL_ADMINISTRADOR) {
            throw new SecurityException("Solo usuarios con rol de administrador pueden realizar esta operacion");
        }
    }

    // Checa que rol, tipoUsuario y programaEducativo existan y esten activos.
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

    // Verifica que nadie mas tenga ese nombre de usuario.
    private void validarUsernameDisponible(String username) {
        if (usuarioRepository.buscarPorUsername(username) != null) {
            throw new IllegalArgumentException("Ya existe un usuario con el username indicado");
        }
    }

    // Verifica que el correo no lo tenga otro usuario. Si estamos editando, ignora al mismo usuario.
    private void validarEmailDisponible(String email, Integer idUsuarioActual) {
        UsuarioPerfil usuario = usuarioRepository.buscarPorEmail(email);
        if (usuario != null && (idUsuarioActual == null || !usuario.getIdUsuario().equals(idUsuarioActual))) {
            throw new IllegalArgumentException("Ya existe un usuario con el correo indicado");
        }
    }

    // Genera una clave unica con formato RGR-XXXXXX. Si se repite, reintenta hasta 10 veces.
    private String generarClaveUsuario() {
        for (int intento = 0; intento < INTENTOS_CLAVE_USUARIO; intento++) {
            String clave = "RGR-" + UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
            if (usuarioRepository.buscarPorClaveUsuario(clave) == null) {
                return clave;
            }
        }
        throw new IllegalStateException("No se pudo generar una clave de usuario unica");
    }

    // Busca al usuario recien creado para devolver su perfil completo.
    // Primero por id, si falla por username.
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

    // El id de usuario no puede ser nulo.
    private void validarIdUsuario(Integer idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("idUsuario es obligatorio");
        }
    }

    // Valida que un id de catalogo no sea nulo ni cero.
    private Integer validarIdCatalogo(Integer valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser mayor a cero");
        }
        return valor;
    }

    // Valida el username y lo pasa a minusculas.
    private String validarUsername(String valor) {
        return validarTextoObligatorio(valor, "usuario", MAX_USERNAME).toLowerCase();
    }

    // Valida que la contrasena no este vacia y respete el maximo de BCrypt (72).
    private String validarPassword(String valor) {
        return validarTextoObligatorio(valor, "contraseña", MAX_PASSWORD_BCRYPT);
    }

    // Valida el formato del correo y lo pasa a minusculas.
    private String validarEmail(String valor) {
        String email = validarTextoObligatorio(valor, "correo", MAX_EMAIL).toLowerCase();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("correo debe tener un formato valido");
        }
        return email;
    }

    // Valida que un campo de texto no este vacio y no exceda el maximo de caracteres.
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

    // Valida un texto opcional: si viene vacio lo deja como null, si viene lo normaliza.
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

    // Ayudante: true si el string no es nulo ni vacio.
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
