import javafx.beans.property.*;

class Produit {
    private SimpleIntegerProperty id;
    private SimpleStringProperty name;
    private SimpleIntegerProperty quantity;
    private SimpleDoubleProperty prix;

    public Produit(int id, String name, int quantity, double prix) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.prix = new SimpleDoubleProperty(prix);
    }



    // Getter pour id
    public int getId() {
        return id.get();
    }

    // Setter pour id
    public void setId(int id) {
        this.id.set(id);
    }

    // Propriété pour id
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    // Getter pour name
    public String getName() {
        return name.get();
    }

    // Setter pour name
    public void setName(String name) {
        this.name.set(name);
    }

    // Propriété pour name
    public SimpleStringProperty nameProperty() {
        return name;
    }

    // Getter pour quantity
    public int getQuantity() {
        return quantity.get();
    }

    // Setter pour quantity
    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    // Propriété pour quantity
    public SimpleIntegerProperty quantityProperty() {
        return quantity;
    }

    // Getter pour prix
    public double getPrix() {
        return prix.get();
    }

    // Setter pour prix
    public void setPrix(double prix) {
        this.prix.set(prix);
    }

    // Propriété pour prix
    public SimpleDoubleProperty prixProperty() {
        return prix;
    }

    @Override
    public String toString() {
        return "Produit [id=" + id.get() + ", nom=" + name.get() + ", prix=" + prix.get() + ", quantité=" + quantity.get() + "]";
    }
}
