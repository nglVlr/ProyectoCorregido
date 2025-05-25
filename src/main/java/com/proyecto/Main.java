package com.proyecto;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {
    // interfaz
    private TableView<Ticket> ticketTable = new TableView<>();
    private TextField tituloField = new TextField();
    private TextArea descripcionArea = new TextArea();
    private ComboBox<String> estadoCombo = new ComboBox<>();
    private ComboBox<Tecnico> tecnicoCombo = new ComboBox<>();
    private ComboBox<Usuario> usuarioCombo = new ComboBox<>();
    private DatePicker fechaCreacionPicker = new DatePicker();

    // DAOs
    private TicketDAO ticketDAO;
    private UsuarioDAO usuarioDAO;

    // Datos del sistema
    private Departamento departamentoIT = new Departamento("IT");
    private Roles rolAdmin = new Roles("Administrador");
    private Roles rolTecnico = new Roles("Técnico");
    private Roles rolUsuario = new Roles("Usuario");
    private Administrador admin = new Administrador("Admin Principal", "admin@empresa.com", rolAdmin);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            ticketDAO = new TicketDAO();
            usuarioDAO = new UsuarioDAO();

            // Configuración inicial de la ventana
            primaryStage.setTitle("Sistema de Tickets - Administración");
            primaryStage.setWidth(1100);
            primaryStage.setHeight(750);

            // Crear datos de prueba
            //crearDatosDemo();

            // Layout principal
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(20));
            root.setStyle("-fx-background-color: #f5f5f5;");

            // panel superior
            Label tituloApp = new Label("Sistema de Gestión de Tickets");
            tituloApp.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            BorderPane.setAlignment(tituloApp, javafx.geometry.Pos.CENTER);
            root.setTop(tituloApp);

            // panel central
            configurarTablaTickets();
            VBox tablaContainer = new VBox(ticketTable);
            tablaContainer.setPadding(new Insets(10));
            tablaContainer.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-padding: 10;");
            root.setCenter(tablaContainer);

            // panel derecho (administracion)
            VBox adminPanel = new VBox(10);
            adminPanel.setPadding(new Insets(15));
            adminPanel.setStyle("-fx-background-color: #ecf0f1; -fx-border-radius: 5; -fx-padding: 15;");

            Button btnGestionarRoles = new Button("Gestionar Roles");
            Button btnGestionarSistema = new Button("Gestionar Sistema");
            Button btnEliminarUsuario = new Button("Eliminar Usuario");
            Button btnReportes = new Button("Generar Reportes");

            // botones de administración
            String botonAdminStyle = "-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;";
            btnGestionarRoles.setStyle(botonAdminStyle);
            btnGestionarSistema.setStyle(botonAdminStyle);
            btnEliminarUsuario.setStyle(botonAdminStyle);
            btnReportes.setStyle(botonAdminStyle);

            adminPanel.getChildren().addAll(
                    new Label("Opciones de Administración:"),
                    btnGestionarRoles,
                    btnGestionarSistema,
                    btnEliminarUsuario,
                    btnReportes
            );
            root.setRight(adminPanel);

            // panel inferior (formulario)
            GridPane formulario = new GridPane();
            formulario.setHgap(10);
            formulario.setVgap(10);
            formulario.setPadding(new Insets(15));
            formulario.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-padding: 15;");

            // Campos del formulario
            formulario.add(new Label("Título:"), 0, 0);
            formulario.add(tituloField, 1, 0);
            formulario.add(new Label("Descripción:"), 0, 1);
            formulario.add(descripcionArea, 1, 1);
            formulario.add(new Label("Estado:"), 0, 2);
            estadoCombo.setItems(FXCollections.observableArrayList("Abierto", "En Progreso", "Cerrado"));
            formulario.add(estadoCombo, 1, 2);
            formulario.add(new Label("Técnico:"), 0, 3);
            tecnicoCombo.setItems(FXCollections.observableList(departamentoIT.buscarTecnicos()));
            formulario.add(tecnicoCombo, 1, 3);
            formulario.add(new Label("Solicitante:"), 0, 4);
            usuarioCombo.setItems(FXCollections.observableArrayList(getUsuarios()));
            formulario.add(usuarioCombo, 1, 4);
            formulario.add(new Label("Fecha Creación:"), 0, 5);
            fechaCreacionPicker.setValue(LocalDate.now());
            formulario.add(fechaCreacionPicker, 1, 5);

            // Botones CRUD
            HBox botonesContainer = new HBox(10);
            Button btnCrear = new Button("Crear Ticket");
            Button btnActualizar = new Button("Actualizar");
            Button btnEliminar = new Button("Eliminar");
            Button btnAgregarNota = new Button("Agregar Nota");
            Button btnLimpiar = new Button("Limpiar");

            // Estilo botones
            String botonStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;";
            btnCrear.setStyle(botonStyle);
            btnActualizar.setStyle(botonStyle + "-fx-background-color: #2ecc71;");
            btnEliminar.setStyle(botonStyle + "-fx-background-color: #e74c3c;");
            btnAgregarNota.setStyle(botonStyle + "-fx-background-color: #f39c12;");
            btnLimpiar.setStyle(botonStyle + "-fx-background-color: #95a5a6;");

            botonesContainer.getChildren().addAll(btnCrear, btnActualizar, btnEliminar, btnAgregarNota, btnLimpiar);
            formulario.add(botonesContainer, 1, 6);

            root.setBottom(formulario);

            // eventos
            btnCrear.setOnAction(e -> crearTicket());
            btnActualizar.setOnAction(e -> actualizarTicket());
            btnEliminar.setOnAction(e -> eliminarTicket());
            btnAgregarNota.setOnAction(e -> agregarNota());
            btnLimpiar.setOnAction(e -> limpiarCampos());
            btnGestionarRoles.setOnAction(e -> gestionarRoles());
            btnGestionarSistema.setOnAction(e -> gestionarSistema());
            btnEliminarUsuario.setOnAction(e -> eliminarUsuario());
            btnReportes.setOnAction(e -> generarReportes());

            // seleccionar ticket de la tabla
            ticketTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    cargarDatosTicket(newSelection);
                }
            });

            // Cargar datos iniciales
            cargarTickets();

            // Mostrar ventana
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (SQLException e) {
            mostrarAlerta("Error de conexión", "No se pudo conectar a la base de datos: " + e.getMessage());
        }
    }

    private void crearDatosDemo() {
        try {
            // Crear técnicos de prueba
            Tecnico tecnico1 = new Tecnico("Juan Pérez", "juan@empresa.com", rolTecnico);
            Tecnico tecnico2 = new Tecnico("María García", "maria@empresa.com", rolTecnico);
            departamentoIT.asignarTecnico(tecnico1);
            departamentoIT.asignarTecnico(tecnico2);

            // Crear usuarios de prueba
            Usuario usuario1 = new Usuario("Carlos López", "carlos@empresa.com", rolUsuario) {
                @Override
                public void mostrarInfo() {
                    System.out.println("Usuario normal: " + getNombre());
                }
            };
            Usuario usuario2 = new Usuario("Ana Martínez", "ana@empresa.com", rolUsuario) {
                @Override
                public void mostrarInfo() {
                    System.out.println("Usuario normal: " + getNombre());
                }
            };

            // Guardar en base de datos
            usuarioDAO.crearUsuario(admin);
            usuarioDAO.crearUsuario(tecnico1);
            usuarioDAO.crearUsuario(tecnico2);
            usuarioDAO.crearUsuario(usuario1);
            usuarioDAO.crearUsuario(usuario2);

            // Crear tickets de prueba
            Ticket ticket1 = new Ticket("Error en sistema", "No puedo acceder al sistema", usuario1, tecnico1);
            Ticket ticket2 = new Ticket("Solicitud de software", "Necesito instalar Photoshop", usuario2, tecnico2);

            ticketDAO.crearTicket(ticket1);
            ticketDAO.crearTicket(ticket2);

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron crear los datos de prueba: " + e.getMessage());
        }
    }

    private void configurarTablaTickets() {
        TableColumn<Ticket, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Ticket, String> tituloCol = new TableColumn<>("Título");
        tituloCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitulo()));

        TableColumn<Ticket, String> estadoCol = new TableColumn<>("Estado");
        estadoCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado().getNombre()));

        TableColumn<Ticket, String> tecnicoCol = new TableColumn<>("Técnico");
        tecnicoCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAsignado().getNombre()));

        TableColumn<Ticket, String> solicitanteCol = new TableColumn<>("Solicitante");
        solicitanteCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSolicitante().getNombre()));

        TableColumn<Ticket, String> fechaCol = new TableColumn<>("Fecha Creación");
        fechaCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaCreacion().toString()));

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

    private List<Usuario> getUsuarios() {
        try {
            return usuarioDAO.listarTodos();
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los usuarios: " + e.getMessage());
            return new ArrayList<>();
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
            if (validarCampos()) {
                Ticket nuevoTicket = new Ticket(
                        tituloField.getText(),
                        descripcionArea.getText(),
                        usuarioCombo.getValue(),
                        tecnicoCombo.getValue()
                );

                nuevoTicket.getEstado().cambiarEstado(estadoCombo.getValue());
                nuevoTicket.setFechaCreacion(fechaCreacionPicker.getValue());

                ticketDAO.crearTicket(nuevoTicket);
                ticketTable.getItems().add(nuevoTicket);
                limpiarCampos();
                mostrarAlerta("Exito", "Ticket creado correctamente");
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo crear el ticket: " + e.getMessage());
        }
    }

    private void actualizarTicket() {
        Ticket seleccionado = ticketTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                if (validarCampos()) {
                    seleccionado.setTitulo(tituloField.getText());
                    seleccionado.setDescripcion(descripcionArea.getText());
                    seleccionado.getEstado().cambiarEstado(estadoCombo.getValue());
                    seleccionado.setAsignado(tecnicoCombo.getValue());
                    seleccionado.setSolicitante(usuarioCombo.getValue());
                    seleccionado.setFechaCreacion(fechaCreacionPicker.getValue());

                    ticketDAO.actualizarTicket(seleccionado);
                    ticketTable.refresh();
                    mostrarAlerta("Exito", "Ticket actualizado correctamente");
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
            confirmacion.setTitle("Confirmar eliminacion");
            confirmacion.setHeaderText("¿Estás seguro de eliminar este ticket?");
            confirmacion.setContentText("Esta acción no se puede deshacer");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                try {
                    ticketDAO.eliminarTicket(seleccionado.getId());
                    ticketTable.getItems().remove(seleccionado);
                    mostrarAlerta("exito", "Ticket eliminado correctamente");
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
            resultado.ifPresent(nota -> {
                try {
                    ticketDAO.agregarNota(seleccionado.getId(), new Nota(nota));
                    mostrarAlerta("Éxito", "Nota agregada correctamente");
                } catch (SQLException e) {
                    mostrarAlerta("Error", "No se pudo agregar la nota: " + e.getMessage());
                }
            });
        } else {
            mostrarAlerta("Error", "Selecciona un ticket para agregar una nota");
        }
    }

    private void generarReportes() {
        try {
            List<Ticket> tickets = ticketTable.getItems();

            long abiertos = tickets.stream()
                    .filter(t -> t.getEstado().getNombre().equals("Abierto"))
                    .count();

            long enProgreso = tickets.stream()
                    .filter(t -> t.getEstado().getNombre().equals("En Progreso"))
                    .count();

            long cerrados = tickets.stream()
                    .filter(t -> t.getEstado().getNombre().equals("Cerrado"))
                    .count();

            String reporte = "Reporte de Tickets:\n\n" +
                    "Total tickets: " + tickets.size() + "\n" +
                    "Abiertos: " + abiertos + "\n" +
                    "En progreso: " + enProgreso + "\n" +
                    "Cerrados: " + cerrados;

            mostrarAlerta("Reporte", reporte);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo generar el reporte: " + e.getMessage());
        }
    }

    private void gestionarRoles() {
        // Implementación de gestión de roles
        mostrarAlerta("En desarrollo", "Función de gestión de roles en desarrollo");
    }

    private void gestionarSistema() {
        // Implementación de gestión del sistema
        mostrarAlerta("En desarrollo", "Función de gestión del sistema en desarrollo");
    }

    private void eliminarUsuario() {
        // Implementación de eliminación de usuario
        mostrarAlerta("En desarrollo", "Función de eliminar usuario en desarrollo");
    }

    private boolean validarCampos() {
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

    private void limpiarCampos() {
        tituloField.clear();
        descripcionArea.clear();
        estadoCombo.getSelectionModel().clearSelection();
        tecnicoCombo.getSelectionModel().clearSelection();
        usuarioCombo.getSelectionModel().clearSelection();
        fechaCreacionPicker.setValue(LocalDate.now());
        ticketTable.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}