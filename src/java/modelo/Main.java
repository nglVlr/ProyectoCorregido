import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.*;

import java.time.LocalDate;
import java.util.List;

public class Main extends Application {
    private TableView<Ticket> ticketTable = new TableView<>();
    private TextField tituloField = new TextField();
    private TextArea descripcionArea = new TextArea();
    private ComboBox<EstadoTicket> estadoCombo = new ComboBox<>();
    private ComboBox<Tecnico> tecnicoCombo = new ComboBox<>();
    private ComboBox<Usuario> usuarioCombo = new ComboBox<>();

    // Datos de prueba (deberías cargarlos de tu base de datos)
    private Departamento departamentoIT = new Departamento("IT");
    private Roles rolAdmin = new Roles("Administrador");
    private Roles rolTecnico = new Roles("Técnico");
    private Administrador admin = new Administrador("Admin Principal", "admin@empresa.com", rolAdmin);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Configuración inicial
        primaryStage.setTitle("Sistema de Tickets - Administración");
        primaryStage.setWidth(1000);
        primaryStage.setHeight(700);

        // Crear técnicos de prueba
        crearTecnicosDemo();

        // Layout principal
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // --- PANEL SUPERIOR ---
        Label tituloApp = new Label("Sistema de Gestion de Tickets");
        tituloApp.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        BorderPane.setAlignment(tituloApp, javafx.geometry.Pos.CENTER);
        root.setTop(tituloApp);

        // --- PANEL CENTRAL (Tabla) ---
        configurarTablaTickets();
        VBox tablaContainer = new VBox(ticketTable);
        tablaContainer.setPadding(new Insets(10));
        root.setCenter(tablaContainer);

        // --- PANEL DERECHO (Opciones de administración) ---
        VBox adminPanel = new VBox(10);
        adminPanel.setPadding(new Insets(15));
        adminPanel.setStyle("-fx-background-color: #ecf0f1; -fx-border-color: #bdc3c7; -fx-border-width: 1px;");

        Button btnGestionarRoles = new Button("Gestionar Roles");
        Button btnGestionarSistema = new Button("Gestionar Sistema");
        Button btnEliminarUsuario = new Button("Eliminar Usuario");

        // Estilo botones admin
        String botonAdminStyle = "-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;";
        btnGestionarRoles.setStyle(botonAdminStyle);
        btnGestionarSistema.setStyle(botonAdminStyle);
        btnEliminarUsuario.setStyle(botonAdminStyle);

        adminPanel.getChildren().addAll(
                new Label("Opciones de Administración:"),
                btnGestionarRoles,
                btnGestionarSistema,
                btnEliminarUsuario
        );
        root.setRight(adminPanel);

        // --- PANEL INFERIOR (Formulario) ---
        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(15));
        formulario.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ddd;");

        // Campos del formulario
        formulario.add(new Label("Título:"), 0, 0);
        formulario.add(tituloField, 1, 0);
        formulario.add(new Label("Descripción:"), 0, 1);
        formulario.add(descripcionArea, 1, 1);
        formulario.add(new Label("Estado:"), 0, 2);
        estadoCombo.setItems(FXCollections.observableArrayList(
                new EstadoTicket("Abierto"),
                new EstadoTicket("En Progreso"),
                new EstadoTicket("Cerrado")
        ));
        formulario.add(estadoCombo, 1, 2);
        formulario.add(new Label("Técnico:"), 0, 3);
        tecnicoCombo.setItems(FXCollections.observableArrayList(departamentoIT.buscarTecnicos()));
        formulario.add(tecnicoCombo, 1, 3);
        formulario.add(new Label("Solicitante:"), 0, 4);
        usuarioCombo.setItems(FXCollections.observableArrayList(admin)); // En realidad debería cargar todos los usuarios
        formulario.add(usuarioCombo, 1, 4);

        // Botones CRUD
        HBox botonesContainer = new HBox(10);
        Button btnCrear = new Button("Crear Ticket");
        Button btnActualizar = new Button("Actualizar");
        Button btnEliminar = new Button("Eliminar");
        Button btnAgregarNota = new Button("Agregar Nota");

        // Estilo botones
        String botonStyle = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;";
        btnCrear.setStyle(botonStyle);
        btnActualizar.setStyle(botonStyle + "-fx-background-color: #2ecc71;");
        btnEliminar.setStyle(botonStyle + "-fx-background-color: #e74c3c;");
        btnAgregarNota.setStyle(botonStyle + "-fx-background-color: #f39c12;");

        botonesContainer.getChildren().addAll(btnCrear, btnActualizar, btnEliminar, btnAgregarNota);
        formulario.add(botonesContainer, 1, 5);

        root.setBottom(formulario);

        // --- EVENTOS ---
        btnCrear.setOnAction(e -> crearTicket());
        btnActualizar.setOnAction(e -> actualizarTicket());
        btnEliminar.setOnAction(e -> eliminarTicket());
        btnAgregarNota.setOnAction(e -> agregarNota());
        btnGestionarRoles.setOnAction(e -> admin.gestionarRoles(rolTecnico));
        btnGestionarSistema.setOnAction(e -> admin.gestionarSistema());
        btnEliminarUsuario.setOnAction(e -> mostrarDialogoEliminarUsuario());

        // Cargar datos iniciales
        cargarTickets();

        // Mostrar ventana
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void crearTecnicosDemo() {
        Tecnico tecnico1 = new Tecnico("Juan Pérez", "juan@empresa.com", rolTecnico);
        Tecnico tecnico2 = new Tecnico("María García", "maria@empresa.com", rolTecnico);
        departamentoIT.asignarTecnico(tecnico1);
        departamentoIT.asignarTecnico(tecnico2);
    }

    private void configurarTablaTickets() {
        TableColumn<Ticket, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());

        TableColumn<Ticket, String> tituloCol = new TableColumn<>("Título");
        tituloCol.setCellValueFactory(cellData -> cellData.getValue().tituloProperty());

        TableColumn<Ticket, String> estadoCol = new TableColumn<>("Estado");
        estadoCol.setCellValueFactory(cellData -> cellData.getValue().estado().nombreProperty());

        TableColumn<Ticket, String> tecnicoCol = new TableColumn<>("Técnico");
        tecnicoCol.setCellValueFactory(cellData -> cellData.getValue().asignadoProperty().getValue().nombreProperty());

        ticketTable.getColumns().addAll(idCol, tituloCol, estadoCol, tecnicoCol);
    }

    private void cargarTickets() {
        // Deberías cargar esto desde tu base de datos
        ticketTable.getItems().clear();
        List<Ticket> tickets = Ticket.cargarTodos();
        ticketTable.getItems().addAll(tickets);
    }

    private void crearTicket() {
        try {
            Ticket nuevoTicket = new Ticket(
                    tituloField.getText(),
                    descripcionArea.getText(),
                    usuarioCombo.getValue(),
                    tecnicoCombo.getValue()
            );

            if (estadoCombo.getValue() != null) {
                nuevoTicket.estado().cambiarEstado(estadoCombo.getValue().getNombre());
            }

            nuevoTicket.guardarEnBD();
            Serializador.guardarTicket(nuevoTicket);
            cargarTickets();
            limpiarCampos();
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Error", e.getMessage());
        }
    }

    private void actualizarTicket() {
        Ticket seleccionado = ticketTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            seleccionado.setDescripcion(descripcionArea.getText());
            if (estadoCombo.getValue() != null) {
                seleccionado.estado().cambiarEstado(estadoCombo.getValue().getNombre());
            }
            if (tecnicoCombo.getValue() != null) {
                seleccionado.setAsignado(tecnicoCombo.getValue());
            }
            // Aquí deberías actualizar también en la base de datos
            cargarTickets();
        }
    }

    private void eliminarTicket() {
        Ticket seleccionado = ticketTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            // Aquí deberías eliminar de la base de datos
            ticketTable.getItems().remove(seleccionado);
        }
    }

    private void agregarNota() {
        Ticket seleccionado = ticketTable.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Agregar Nota");
            dialog.setHeaderText("Ingrese la nota para el ticket #" + seleccionado.getId());
            dialog.setContentText("Nota:");

            dialog.showAndWait().ifPresent(nota -> {
                seleccionado.agregarNota(new Nota(nota));
                // Aquí deberías guardar el cambio en la base de datos
            });
        }
    }

    private void mostrarDialogoEliminarUsuario() {
        // Implementar diálogo para seleccionar usuario a eliminar
    }

    private void limpiarCampos() {
        tituloField.clear();
        descripcionArea.clear();
        estadoCombo.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
