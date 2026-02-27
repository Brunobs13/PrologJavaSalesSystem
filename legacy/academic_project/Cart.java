import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Cliente cliente;
    private List<Item> items;

    public Cart(Cliente cliente) {
        this.cliente = cliente;
        this.items = new ArrayList<>();
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void addItem(Item item, int quantidadeDisponivel) {
        if (item.getQuantidade() > quantidadeDisponivel) {
            System.out.println("Quantidade solicitada excede o estoque disponível.");
            return;
        }
        for (Item i : items) {
            if (i.getId() == item.getId()) {
                int novaQuantidade = i.getQuantidade() + item.getQuantidade();
                if (novaQuantidade > quantidadeDisponivel) {
                    System.out.println("Quantidade solicitada excede o estoque disponível.");
                    return;
                }
                i.setQuantidade(novaQuantidade);
                return;
            }
        }
        items.add(item);
    }

    public void removeItem(int itemId) {
        items.removeIf(item -> item.getId() == itemId);
    }

    public List<Item> getItems() {
        return this.items;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cliente: ").append(cliente.getNome()).append("\n");
        sb.append("Itens:\n");
        for (Item item : items) {
            sb.append(item).append("\n");
        }
        return sb.toString();
    }
}
