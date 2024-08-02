import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ressource/MainController.fxml")));
        primaryStage.setTitle("Gestion des Produits");

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void showAddProductDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un produit");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField quantityField = new TextField();
        TextField priceField = new TextField();
        TextField additionalField = new TextField();

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Electronique", "Alimentaire", "Vestimentaire");
        typeBox.setValue("Electronique");

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("ID:"), 0, 1);
        grid.add(idField, 1, 1);
        grid.add(new Label("Nom:"), 0, 2);
        grid.add(nameField, 1, 2);
        grid.add(new Label("Quantité:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Prix:"), 0, 4);
        grid.add(priceField, 1, 4);
        grid.add(new Label("Détails additionnels:"), 0, 5);
        grid.add(additionalField, 1, 5);

        dialog.getDialogPane().setContent(grid);
        ButtonType addButtonType = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String type = typeBox.getValue();
                int id = Integer.parseInt(idField.getText());
                String name = nameField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                double price = Double.parseDouble(priceField.getText());

                try (Connection connection = DatabaseConnection.getConnection()) {
                    PreparedStatement preparedStatement;
                    if (type.equals("Electronique")) {
                        preparedStatement = connection.prepareStatement(
                                "INSERT INTO produits_electroniques (id, nom, quantite, garantie, prix) VALUES (?, ?, ?, ?, ?)");
                        preparedStatement.setInt(4, Integer.parseInt(additionalField.getText())); // garantie
                    } else if (type.equals("Alimentaire")) {
                        preparedStatement = connection.prepareStatement(
                                "INSERT INTO produits_alimentaires (id, nom, quantite, dateexpiration, prix) VALUES (?, ?, ?, ?, ?)");
                        preparedStatement.setString(4, additionalField.getText()); // date d'expiration
                    } else {
                        preparedStatement = connection.prepareStatement(
                                "INSERT INTO produits_vestimentaires (id, nom, quantite, taille, prix) VALUES (?, ?, ?, ?, ?)");
                        preparedStatement.setString(4, additionalField.getText()); // taille
                    }
                    preparedStatement.setInt(1, id);
                    preparedStatement.setString(2, name);
                    preparedStatement.setInt(3, quantity);
                    preparedStatement.setDouble(5, price);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Produit ajouté avec succès.");
                    } else {
                        System.out.println("Erreur lors de l'ajout du produit.");
                    }
                } catch (SQLException e) {
                    System.out.println("Erreur SQL : " + e.getMessage());
                }
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }
    public void showDeleteProductDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Supprimer un produit");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Electronique", "Alimentaire", "Vestimentaire");
        typeBox.setValue("Electronique");

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("ID:"), 0, 1);
        grid.add(idField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        ButtonType deleteButtonType = new ButtonType("Supprimer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteButtonType) {
                int id = Integer.parseInt(idField.getText());
                String type = typeBox.getValue();
                String tableName = "";

                switch (type) {
                    case "Electronique":
                        tableName = "produits_electroniques";
                        break;
                    case "Alimentaire":
                        tableName = "produits_alimentaires";
                        break;
                    case "Vestimentaire":
                        tableName = "produits_vestimentaires";
                        break;
                }

                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(
                             "DELETE FROM " + tableName + " WHERE id = ?")) {

                    preparedStatement.setInt(1, id);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        if (Magasin.removeProduct(id)) {
                            System.out.println("Produit supprimé avec succès.");
                        } else {
                            System.out.println("Produit supprimée de la base de données.");
                        }
                    } else {
                        System.out.println("Produit non trouvé dans la base de données.");
                    }
                } catch (SQLException e) {
                    System.out.println("Erreur SQL : " + e.getMessage());
                }
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }


     public void showUpdateProductDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Modifier un produit");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField quantityField = new TextField();
        TextField priceField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Electronique", "Alimentaire", "Vestimentaire");
        typeBox.setValue("Electronique");

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("ID:"), 0, 1);
        grid.add(idField, 1, 1);
        grid.add(new Label("Nouveau Nom:"), 0, 2);
        grid.add(nameField, 1, 2);
        grid.add(new Label("Nouvelle Quantité:"), 0, 3);
        grid.add(quantityField, 1, 3);
        grid.add(new Label("Nouveau Prix:"), 0, 4);
        grid.add(priceField, 1, 4);

        dialog.getDialogPane().setContent(grid);
        ButtonType updateButtonType = new ButtonType("Mettre à jour", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                int id = Integer.parseInt(idField.getText());
                String newName = nameField.getText();
                int newQuantity = Integer.parseInt(quantityField.getText());
                double newPrice = Double.parseDouble(priceField.getText());
                String type = typeBox.getValue();
                String tableName = "";

                switch (type) {
                    case "Electronique":
                        tableName = "produits_electroniques";
                        break;
                    case "Alimentaire":
                        tableName = "produits_alimentaires";
                        break;
                    case "Vestimentaire":
                        tableName = "produits_vestimentaires";
                        break;
                }

                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(
                             "UPDATE " + tableName + " SET nom = ?, quantite = ?, prix = ? WHERE id = ?")) {

                    preparedStatement.setString(1, newName);
                    preparedStatement.setInt(2, newQuantity);
                    preparedStatement.setDouble(3, newPrice);
                    preparedStatement.setInt(4, id);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Produit mis à jour avec succès.");
                    } else {
                        System.out.println("Produit non trouvé.");
                    }
                } catch (SQLException e) {
                    System.out.println("Erreur SQL : " + e.getMessage());
                }
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }


    public void showUpdateQuantityDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Modifier la quantité d'un produit");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        TextField quantityField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Electronique", "Alimentaire", "Vestimentaire");
        typeBox.setValue("Electronique");

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("ID:"), 0, 1);
        grid.add(idField, 1, 1);
        grid.add(new Label("Nouvelle Quantité:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType updateButtonType = new ButtonType("Mettre à jour", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                int id = Integer.parseInt(idField.getText());
                int newQuantity = Integer.parseInt(quantityField.getText());
                String type = typeBox.getValue();
                String tableName = "";

                switch (type) {
                    case "Electronique":
                        tableName = "produits_electroniques";
                        break;
                    case "Alimentaire":
                        tableName = "produits_alimentaires";
                        break;
                    case "Vestimentaire":
                        tableName = "produits_vestimentaires";
                        break;
                }

                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(
                             "UPDATE " + tableName + " SET quantite = ? WHERE id = ?")) {

                    preparedStatement.setInt(1, newQuantity);
                    preparedStatement.setInt(2, id);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Quantité mise à jour avec succès.");
                    } else {
                        System.out.println("Produit non trouvé.");
                    }
                } catch (SQLException e) {
                    System.out.println("Erreur SQL : " + e.getMessage());
                }
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }

    public void showSearchProductDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Rechercher un produit");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Electronique", "Alimentaire", "Vestimentaire");
        typeBox.setValue("Electronique");

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("Nom du produit:"), 0, 1);
        grid.add(nameField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        ButtonType searchButtonType = new ButtonType("Rechercher", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                String name = nameField.getText();
                String type = typeBox.getValue();
                String tableName = "";

                switch (type) {
                    case "Electronique":
                        tableName = "produits_electroniques";
                        break;
                    case "Alimentaire":
                        tableName = "produits_alimentaires";
                        break;
                    case "Vestimentaire":
                        tableName = "produits_vestimentaires";
                        break;
                }

                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(
                             "SELECT * FROM " + tableName + " WHERE nom LIKE ?")) {

                    preparedStatement.setString(1, "%" + name + "%");
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String productName = resultSet.getString("nom");
                        int quantity = resultSet.getInt("quantite");
                        double price = resultSet.getDouble("prix");

                        System.out.println("ID: " + id + ", Nom: " + productName + ", Quantité: " + quantity + ", Prix: " + price);
                    }
                } catch (SQLException e) {
                    System.out.println("Erreur SQL : " + e.getMessage());
                }
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }


    public void showListProductDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Lister les produits par lettre");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField letterField = new TextField();
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Electronique", "Alimentaire", "Vestimentaire");
        typeBox.setValue("Electronique");

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);
        grid.add(new Label("Lettre initiale:"), 0, 1);
        grid.add(letterField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        ButtonType listButtonType = new ButtonType("Lister", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(listButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == listButtonType) {
                String letter = letterField.getText() + "%";
                String type = typeBox.getValue();
                String tableName = "";

                switch (type) {
                    case "Electronique":
                        tableName = "produits_electroniques";
                        break;
                    case "Alimentaire":
                        tableName = "produits_alimentaires";
                        break;
                    case "Vestimentaire":
                        tableName = "produits_vestimentaires";
                        break;
                }

                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(
                             "SELECT * FROM " + tableName + " WHERE nom LIKE ?")) {

                    preparedStatement.setString(1, letter);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("nom");
                        int quantity = resultSet.getInt("quantite");
                        double price = resultSet.getDouble("prix");

                        System.out.println("ID: " + id + ", Nom: " + name + ", Quantité: " + quantity + ", Prix: " + price);
                    }
                } catch (SQLException e) {
                    System.out.println("Erreur SQL : " + e.getMessage());
                }
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }


    public void showDisplayByTypeDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Afficher les produits par type");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Electronique", "Alimentaire", "Vestimentaire");
        typeBox.setValue("Electronique");

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeBox, 1, 0);

        dialog.getDialogPane().setContent(grid);
        ButtonType displayButtonType = new ButtonType("Afficher", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(displayButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == displayButtonType) {
                String type = typeBox.getValue();
                String tableName = "";

                switch (type) {
                    case "Electronique":
                        tableName = "produits_electroniques";
                        break;
                    case "Alimentaire":
                        tableName = "produits_alimentaires";
                        break;
                    case "Vestimentaire":
                        tableName = "produits_vestimentaires";
                        break;
                }

                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(
                             "SELECT * FROM " + tableName)) {

                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("nom");
                        int quantity = resultSet.getInt("quantite");
                        double price = resultSet.getDouble("prix");
                        String additionalInfo = type.equals("Electronique") ? "Garantie: " + resultSet.getInt("garantie") :
                                type.equals("Alimentaire") ? "Date d'expiration: " + resultSet.getString("dateexpiration") :
                                        "Taille: " + resultSet.getString("taille");

                        System.out.println(type + ": " + id + ", Nom: " + name + ", Quantité: " + quantity + ", Prix: " + price + ", " + additionalInfo);
                    }
                } catch (SQLException e) {
                    System.out.println("Erreur SQL : " + e.getMessage());
                }
                return null;
            }
            return null;
        });

        dialog.showAndWait();
    }
}
