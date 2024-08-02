import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {

    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button updateQuantityButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button listButton;

    @FXML
    private Button displayByTypeButton;

    @FXML
    private Button quitButton;
    @FXML
    private TableView<Produit> tableView;

    @FXML
    private TableColumn<Produit, Integer> idColumn;

    @FXML
    private TableColumn<Produit, String> nameColumn;

    @FXML
    private TableColumn<Produit, Integer> quantityColumn;

    @FXML
    private TableColumn<Produit, Double> priceColumn;
    @FXML
    private void handleAddButtonAction(ActionEvent event) {
        Main mainApp = new Main();
        mainApp.showAddProductDialog();
    }
    @FXML
    private void handleDeleteButtonAction(ActionEvent event){
        Main mainApp= new Main();
        mainApp.showDeleteProductDialog();
    }
    @FXML
    private  void handleUpdateButtonAction(ActionEvent event){
        Main mainApp = new Main();
        mainApp.showUpdateProductDialog();
    }
    @FXML
    private void handleUpdateQuantityButtonAction(ActionEvent event){
        Main mainApp = new Main();
        mainApp.showUpdateQuantityDialog();
    }
    @FXML
    private void handleSearchButtonAction(ActionEvent event){
        Main mainApp = new Main();
        mainApp.showSearchProductDialog();
    }
    @FXML
    private void handleListButtonAction(ActionEvent event){
        Main mainApp = new Main();
        mainApp.showListProductDialog();
    }

    @FXML
    private void handleDisplayByTypeButtonAction(ActionEvent event) {
        Main mainApp = new Main();
        mainApp.showDisplayByTypeDialog();
    }

    @FXML
    private void handleQuitButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quitter");
        alert.setHeaderText(null);
        alert.setContentText("Au revoir");
        alert.showAndWait();
        System.exit(0);
    }
}
