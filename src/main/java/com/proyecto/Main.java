package com.proyecto;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main extends Application {
    // Componentes para Tickets
    private TableView<Ticket> ticketTable = new TableView<>();
    private TextField tituloField = new TextField();
    private TextArea descripcionArea = new TextArea();
    private ComboBox<String> estadoCombo = new ComboBox<>();
    private ComboBox<Tecnico> tecnicoCombo = new ComboBox<>();
    private ComboBox<Usuario> usuarioCombo = new ComboBox<>();
    private DatePicker fechaCreacionPicker = new DatePicker();

    // Componentes para Usuarios
    private TableView<Usuario> usuariosTable = new TableView<>();
    private TextField nombreUsuarioField = new TextField();
    private TextField correoField = new TextField();
    private ComboBox<String> rolCombo = new ComboBox<>();
    private TextField departamentoField = new TextField();

    // DAOs
    private TicketDAO ticketDAO;
    private UsuarioDAO usuarioDAO;

    // Roles y departamentos
    private Departamento departamentoIT = new Departamento("IT");
    private Roles rolAdmin = new Roles("Administrador");
    private Roles rolTecnico = new Roles("Técnico");
    private Roles rolUsuario = new Roles("Usuario");
    private Administrador admin = new Administrador("Admin Principal", "admin@empresa.com", rolAdmin);

    // Pestañas
    private TabPane tabPane = new TabPane();
    private Tab tabTickets = new Tab("Gestión de Tickets");
    private Tab tabUsuarios = new Tab("Gestión de Usuarios");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            ticketDAO = new TicketDAO();
            usuarioDAO = new UsuarioDAO();

            primaryStage.setTitle("Sistema Integrado de Tickets y Usuarios");
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);

            crearDatosDemo();
            configurarInterfaz();

            Scene scene = new Scene(tabPane);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (SQLException e) {
            mostrarAlerta("Error de conexión", "No se pudo conectar a la base de datos: " + e.getMessage());
        }
    }

    private void configurarInterfaz() {
        configurarInterfazTickets();
        configurarInterfazUsuarios();

        tabPane.getTabs().addAll(tabTickets, tabUsuarios);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }

    private void configurarInterfazTickets() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label tituloApp = new Label("Sistema de Gestión de Tickets");
        tituloApp.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        BorderPane.setAlignment(tituloApp, javafx.geometry.Pos.CENTER);
        root.setTop(tituloApp);

        configurarTablaTickets();
        VBox tablaContainer = new VBox(ticketTable);
        tablaContainer.setPadding(new Insets(10));
        tablaContainer.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-padding: 10;");
        root.setCenter(tablaContainer);

        VBox adminPanel = new VBox(10);
        adminPanel.setPadding(new Insets(15));
        adminPanel.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 5; -fx-padding: 15;");

        Button btnReportes = new Button("Generar Reportes");
        btnReportes.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        adminPanel.getChildren().addAll(new Label("Opciones:"), btnReportes);
        root.setRight(adminPanel);

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(15));
        formulario.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-padding: 15;");

        formulario.add(new Label("Título:"), 0, 0);
        formulario.add(tituloField, 1, 0);
        formulario.add(new Label("Descripción:"), 0, 1);
        descripcionArea.setPrefRowCount(3);
        formulario.add(descripcionArea, 1, 1);
        formulario.add(new Label("Estado:"), 0, 2);
        estadoCombo.setItems(FXCollections.observableArrayList("Abierto", "En Progreso", "Cerrado"));
        formulario.add(estadoCombo, 1, 2);
        formulario.add(new Label("Técnico:"), 0, 3);
        formulario.add(tecnicoCombo, 1, 3);
        formulario.add(new Label("Solicitante:"), 0, 4);
        formulario.add(usuarioCombo, 1, 4);
        formulario.add(new Label("Fecha Creación:"), 0, 5);
        fechaCreacionPicker.setValue(LocalDate.now());
        formulario.add(fechaCreacionPicker, 1, 5);

        HBox botonesContainer = new HBox(10);
        Button btnCrear = new Button("Crear Ticket");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnAgregarNota = new Button("Agregar Nota");
        Button btnLimpiar = new Button("Limpiar");

        String botonStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;";
        btnCrear.setStyle(botonStyle);
        btnActualizar.setStyle(botonStyle + "-fx-background-color: #2ecc71;");
        btnEliminar.setStyle(botonStyle + "-fx-background-color: #e74c3c;");
        btnAgregarNota.setStyle(botonStyle + "-fx-background-color: #f39c12;");
        btnLimpiar.setStyle(botonStyle + "-fx-background-color: #95a5a6;");

        botonesContainer.getChildren().addAll(btnCrear, btnActualizar, btnEliminar, btnAgregarNota, btnLimpiar);
        formulario.add(botonesContainer, 1, 6);

        root.setBottom(formulario);

        btnCrear.setOnAction(e -> crearTicket());
        btnActualizar.setOnAction(e -> actualizarTicket());
        btnEliminar.setOnAction(e -> eliminarTicket());
        btnAgregarNota.setOnAction(e -> agregarNota());
        btnLimpiar.setOnAction(e -> limpiarCamposTicket());
        btnReportes.setOnAction(e -> generarReportes());

        ticketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosTicket(newSelection);
            }
        });

        cargarTickets();
        cargarUsuarios();
        cargarTecnicos();

        tabTickets.setContent(root);
    }

    private void configurarInterfazUsuarios() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        Label titulo = new Label("Gestión de Usuarios del Sistema");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        BorderPane.setAlignment(titulo, javafx.geometry.Pos.CENTER);
        root.setTop(titulo);

        configurarTablaUsuarios();
        VBox tablaContainer = new VBox(usuariosTable);
        tablaContainer.setPadding(new Insets(10));
        tablaContainer.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-padding: 10;");
        root.setCenter(tablaContainer);

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(15);
        formulario.setPadding(new Insets(15));
        formulario.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-padding: 15;");

        Label lblFormulario = new Label("Crear/Editar Usuario");
        lblFormulario.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
        formulario.add(lblFormulario, 0, 0, 2, 1);

        formulario.add(new Label("Nombre:"), 0, 1);
        nombreUsuarioField.setPromptText("Ingrese el nombre completo");
        formulario.add(nombreUsuarioField, 1, 1);

        formulario.add(new Label("Correo:"), 0, 2);
        correoField.setPromptText("usuario@empresa.com");
        formulario.add(correoField, 1, 2);

        formulario.add(new Label("Rol:"), 0, 3);
        rolCombo.setItems(FXCollections.observableArrayList("Usuario", "Técnico", "Administrador"));
        rolCombo.setPromptText("Seleccione un rol");
        formulario.add(rolCombo, 1, 3);

        formulario.add(new Label("Departamento:"), 0, 4);
        departamentoField.setPromptText("Solo para técnicos (opcional)");
        departamentoField.setDisable(true);
        formulario.add(departamentoField, 1, 4);

        HBox botonesContainer = new HBox(10);
        Button btnCrear = new Button("Crear Usuario");
        Button btnActualizar = new Button("Actualizar Usuario");
        Button btnEliminar = new Button("Eliminar Usuario");
        Button btnLimpiar = new Button("Limpiar Campos");

        btnCrear.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnActualizar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnLimpiar.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");

        botonesContainer.getChildren().addAll(btnCrear, btnActualizar, btnEliminar, btnLimpiar);
        formulario.add(botonesContainer, 1, 5);

        root.setBottom(formulario);

        btnCrear.setOnAction(e -> crearUsuario());
        btnActualizar.setOnAction(e -> actualizarUsuario());
        btnEliminar.setOnAction(e -> eliminarUsuario());
        btnLimpiar.setOnAction(e -> limpiarCamposUsuario());

        rolCombo.setOnAction(e -> {
            if ("Técnico".equals(rolCombo.getValue())) {
                departamentoField.setDisable(false);
            } else {
                departamentoField.setDisable(true);
                departamentoField.clear();
            }
        });

        usuariosTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDatosUsuario(newSelection);
            }
        });

        cargarUsuariosParaTabla();

        tabUsuarios.setContent(root);
    }

    private void crearDatosDemo() {
        try {
            admin.setId(1);
            usuarioDAO.crearUsuario(admin);

            Tecnico tecnico1 = new Tecnico("Juan Pérez", "juan@empresa.com", rolTecnico);
            Tecnico tecnico2 = new Tecnico("María García", "maria@empresa.com", rolTecnico);
            usuarioDAO.crearUsuario(tecnico1);
            usuarioDAO.crearUsuario(tecnico2);
            departamentoIT.asignarTecnico(tecnico1);
            departamentoIT.asignarTecnico(tecnico2);

            Usuario usuario1 = new Usuario("Carlos López", "carlos@empresa.com", rolUsuario);
            Usuario usuario2 = new Usuario("Ana Martínez", "ana@empresa.com", rolUsuario);

            usuarioDAO.crearUsuario(usuario1);
            usuarioDAO.crearUsuario(usuario2);

            Ticket ticket1 = new Ticket("Error en sistema", "No puedo acceder al sistema", usuario1, tecnico1);
            Ticket ticket2 = new Ticket("Solicitud de software", "Necesito instalar Photoshop", usuario2, tecnico2);

            ticketDAO.crearTicket(ticket1);
            ticketDAO.crearTicket(ticket2);

        } catch (SQLException e) {
            System.out.println("Error al crear datos demo: " + e.getMessage());
        }
    }

    private void configurarTablaTickets() {
        TableColumn<Ticket, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Ticket, String> tituloCol = new TableColumn<>("Título");
        tituloCol.setCellValueFactory(new PropertyValueFactory<>("titulo"));

        TableColumn<Ticket, String> estadoCol = new TableColumn<>("Estado");
        estadoCol.setCellValueFactory(new PropertyValueFactory<>("estado"));

        TableColumn<Ticket, String> tecnicoCol = new TableColumn<>("Técnico");
        tecnicoCol.setCellValueFactory(new PropertyValueFactory<>("asignado"));

        TableColumn<Ticket, String> solicitanteCol = new TableColumn<>("Solicitante");
        solicitanteCol.setCellValueFactory(new PropertyValueFactory<>("solicitante"));

        TableColumn<Ticket, String> fechaCol = new TableColumn<>("Fecha Creación");
        fechaCol.setCellValueFactory(new PropertyValueFactory<>("fechaCreacion"));

        ticketTable.getColumns().addAll(idCol, tituloCol, estadoCol, tecnicoCol, solicitanteCol, fechaCol);
        ticketTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void cargarTickets() {
        try {
            ticketTable.getItems().clear();
            List<Ticket> tickets = ticketDAO.obtenerTodos();
            ticketTable.getItems().addAll(tickets);
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los tickets: " + e.getMessage());
        }
    }

    private void cargarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            usuarioCombo.setItems(FXCollections.observableArrayList(usuarios));
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }

    private void cargarTecnicos() {
        try {
            List<Tecnico> tecnicos = usuarioDAO.listarTecnicos();
            tecnicoCombo.setItems(FXCollections.observableArrayList(tecnicos));
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los técnicos: " + e.getMessage());
        }
    }

    private void cargarDatosTicket(Ticket ticket) {
        tituloField.setText(ticket.getTitulo());
        descripcionArea.setText(ticket.getDescripcion());
        estadoCombo.setValue(ticket.getEstado().getNombre());
        tecnicoCombo.setValue(ticket.getAsignado());
        usuarioCombo.setValue(ticket.getSolicitante());
        fechaCreacionPicker.setValue(ticket.getFechaCreacion());
    }

    private void crearTicket() {
        try {
            if (validarCamposTicket()) {
                Ticket nuevoTicket = new Ticket(
                        tituloField.getText(),
                        descripcionArea.getText(),
                        usuarioCombo.getValue(),
                        tecnicoCombo.getValue()
                );
                nuevoTicket.getEstado().cambiarEstado(estadoCombo.getValue());
                nuevoTicket.setFechaCreacion(fechaCreacionPicker.getValue());

                ticketDAO.crearTicket(nuevoTicket);
                cargarTickets();
                limpiarCamposTicket();
                mostrarAlerta("Éxito", "Ticket creado correctamente");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo crear el ticket: " + e.getMessage());
        }
    }

    private void actualizarTicket() {
        Ticket seleccionado = ticketTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                if (validarCamposTicket()) {
                    seleccionado.setTitulo(tituloField.getText());
                    seleccionado.setDescripcion(descripcionArea.getText());
                    seleccionado.getEstado().cambiarEstado(estadoCombo.getValue());
                    seleccionado.setAsignado(tecnicoCombo.getValue());
                    seleccionado.setSolicitante(usuarioCombo.getValue());
                    seleccionado.setFechaCreacion(fechaCreacionPicker.getValue());

                    ticketDAO.actualizarTicket(seleccionado);
                    cargarTickets();
                    mostrarAlerta("Éxito", "Ticket actualizado correctamente");
                }
            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo actualizar el ticket: " + e.getMessage());
            }
        } else {
            mostrarAlerta("Error", "Selecciona un ticket para actualizar");
        }
    }

    private void eliminarTicket() {
        Ticket seleccionado = ticketTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Estás seguro de eliminar este ticket?");
            confirmacion.setContentText("Esta acción no se puede deshacer");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    ticketDAO.eliminarTicket(seleccionado.getId());
                    cargarTickets();
                    limpiarCamposTicket();
                    mostrarAlerta("Éxito", "Ticket eliminado correctamente");
                } catch (Exception e) {
                    mostrarAlerta("Error", "No se pudo eliminar el ticket: " + e.getMessage());
                }
            }
        } else {
            mostrarAlerta("Error", "Selecciona un ticket para eliminar");
        }
    }

    private void agregarNota() {
        Ticket seleccionado = ticketTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Agregar Nota");
            dialog.setHeaderText("Ingrese la nota para el ticket #" + seleccionado.getId());
            dialog.setContentText("Nota:");

            Optional<String> resultado = dialog.showAndWait();
            if (resultado.isPresent()) {
                try {
                    ticketDAO.agregarNota(seleccionado.getId(), new Nota(resultado.get()));
                    mostrarAlerta("Éxito", "Nota agregada correctamente");
                } catch (SQLException e) {
                    mostrarAlerta("Error", "No se pudo agregar la nota: " + e.getMessage());
                }
            }
        } else {
            mostrarAlerta("Error", "Selecciona un ticket para agregar una nota");
        }
    }

    private void generarReportes() {
        try {
            List<Ticket> tickets = ticketDAO.obtenerTodos();
            StringBuilder reporte = new StringBuilder();

            reporte.append("=== REPORTE DE TICKETS ===\n\n");
            reporte.append("Total tickets: ").append(tickets.size()).append("\n");

            int abiertos = 0, enProgreso = 0, cerrados = 0;
            for (Ticket ticket : tickets) {
                switch(ticket.getEstado().getNombre()) {
                    case "Abierto": abiertos++; break;
                    case "En Progreso": enProgreso++; break;
                    case "Cerrado": cerrados++; break;
                }
            }

            reporte.append("Abiertos: ").append(abiertos).append("\n");
            reporte.append("En progreso: ").append(enProgreso).append("\n");
            reporte.append("Cerrados: ").append(cerrados).append("\n");

            TextArea area = new TextArea(reporte.toString());
            area.setEditable(false);

            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Reporte de Tickets");
            dialog.setHeaderText("Estadísticas del sistema");
            dialog.getDialogPane().setContent(area);
            dialog.getDialogPane().setPrefSize(400, 300);
            dialog.showAndWait();
        } catch (SQLException e) {
            mostrarAlerta("Error", "Error al generar reporte: " + e.getMessage());
        }
    }

    private boolean validarCamposTicket() {
        if (tituloField.getText().isEmpty()) {
            mostrarAlerta("Error", "El título es obligatorio");
            return false;
        }
        if (descripcionArea.getText().isEmpty()) {
            mostrarAlerta("Error", "La descripción es obligatoria");
            return false;
        }
        if (estadoCombo.getValue() == null) {
            mostrarAlerta("Error", "Debes seleccionar un estado");
            return false;
        }
        if (tecnicoCombo.getValue() == null) {
            mostrarAlerta("Error", "Debes seleccionar un técnico");
            return false;
        }
        if (usuarioCombo.getValue() == null) {
            mostrarAlerta("Error", "Debes seleccionar un solicitante");
            return false;
        }
        return true;
    }

    private void limpiarCamposTicket() {
        tituloField.clear();
        descripcionArea.clear();
        estadoCombo.getSelectionModel().clearSelection();
        tecnicoCombo.getSelectionModel().clearSelection();
        usuarioCombo.getSelectionModel().clearSelection();
        fechaCreacionPicker.setValue(LocalDate.now());
        ticketTable.getSelectionModel().clearSelection();
    }

    private void configurarTablaUsuarios() {
        TableColumn<Usuario, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Usuario, String> nombreCol = new TableColumn<>("Nombre");
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Usuario, String> correoCol = new TableColumn<>("Correo");
        correoCol.setCellValueFactory(new PropertyValueFactory<>("correo"));

        TableColumn<Usuario, String> rolCol = new TableColumn<>("Rol");
        rolCol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        usuariosTable.getColumns().addAll(idCol, nombreCol, correoCol, rolCol);
        usuariosTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void cargarUsuariosParaTabla() {
        try {
            usuariosTable.getItems().clear();
            List<Usuario> usuarios = usuarioDAO.listarTodos();
            usuariosTable.getItems().addAll(usuarios);
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }

    private void cargarDatosUsuario(Usuario usuario) {
        nombreUsuarioField.setText(usuario.getNombre());
        correoField.setText(usuario.getCorreo());
        rolCombo.setValue(usuario.getRol().getNombre());

        if (usuario instanceof Tecnico) {
            departamentoField.setDisable(false);
            departamentoField.setText(((Tecnico)usuario).getDepartamento());
        } else {
            departamentoField.setDisable(true);
            departamentoField.clear();
        }
    }

    private void crearUsuario() {
        try {
            if (validarCamposUsuario()) {
                String nombre = nombreUsuarioField.getText().trim();
                String correo = correoField.getText().trim();
                String rolSeleccionado = rolCombo.getValue();
                String departamento = departamentoField.getText().trim();

                Roles rol = obtenerRol(rolSeleccionado);
                Usuario nuevoUsuario;

                switch(rolSeleccionado) {
                    case "Administrador":
                        nuevoUsuario = new Administrador(nombre, correo, rol);
                        break;
                    case "Técnico":
                        if (departamento.isEmpty()) {
                            nuevoUsuario = new Tecnico(nombre, correo, rol);
                        } else {
                            nuevoUsuario = new Tecnico(nombre, correo, rol, departamento);
                        }
                        break;
                    default:
                        nuevoUsuario = new Usuario(nombre, correo, rol);
                }

                usuarioDAO.crearUsuario(nuevoUsuario);
                cargarUsuariosParaTabla();
                cargarUsuarios();
                cargarTecnicos();
                limpiarCamposUsuario();
                mostrarAlerta("Éxito", "Usuario creado correctamente: " + nombre);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo crear el usuario: " + e.getMessage());
        }
    }

    private void actualizarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                if (validarCamposUsuario()) {
                    seleccionado.setNombre(nombreUsuarioField.getText().trim());
                    seleccionado.setCorreo(correoField.getText().trim());
                    seleccionado.setRol(obtenerRol(rolCombo.getValue()));

                    if (seleccionado instanceof Tecnico) {
                        ((Tecnico)seleccionado).setDepartamento(departamentoField.getText().trim());
                    }

                    usuarioDAO.actualizarUsuario(seleccionado);
                    cargarUsuariosParaTabla();
                    cargarUsuarios();
                    cargarTecnicos();
                    mostrarAlerta("Éxito", "Usuario actualizado correctamente");
                }
            } catch (Exception e) {
                mostrarAlerta("Éxito", "Usuario actualizado correctamente");
            }
        } else {
            mostrarAlerta("Error", "Selecciona un usuario para actualizar");
        }
    }

    private void eliminarUsuario() {
        Usuario seleccionado = usuariosTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            if (seleccionado.getRol().getNombre().equals("Administrador")) {
                mostrarAlerta("Error", "No se puede eliminar un administrador");
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Estás seguro de eliminar este usuario?");
            confirmacion.setContentText("Usuario: " + seleccionado.getNombre() + "\nEsta acción no se puede deshacer");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    usuarioDAO.eliminarUsuario(seleccionado.getId());
                    cargarUsuariosParaTabla();
                    cargarUsuarios();
                    cargarTecnicos();
                    limpiarCamposUsuario();
                    mostrarAlerta("Éxito", "Usuario eliminado correctamente");
                } catch (Exception e) {
                    mostrarAlerta("Error", "No se pudo eliminar el usuario: " + e.getMessage());
                }
            }
        } else {
            mostrarAlerta("Error", "Selecciona un usuario para eliminar");
        }
    }

    private Roles obtenerRol(String nombreRol) {
        switch(nombreRol) {
            case "Administrador":
                return rolAdmin;
            case "Técnico":
                return rolTecnico;
            default:
                return rolUsuario;
        }
    }

    private boolean validarCamposUsuario() {
        if (nombreUsuarioField.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El nombre es obligatorio");
            return false;
        }
        if (correoField.getText().trim().isEmpty()) {
            mostrarAlerta("Error", "El correo es obligatorio");
            return false;
        }
        if (!correoField.getText().contains("@")) {
            mostrarAlerta("Error", "El correo debe tener un formato válido");
            return false;
        }
        if (rolCombo.getValue() == null) {
            mostrarAlerta("Error", "Debes seleccionar un rol");
            return false;
        }
        return true;
    }

    private void limpiarCamposUsuario() {
        nombreUsuarioField.clear();
        correoField.clear();
        rolCombo.getSelectionModel().clearSelection();
        departamentoField.clear();
        departamentoField.setDisable(true);
        usuariosTable.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}