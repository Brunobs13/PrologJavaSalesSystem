public class Item {
    private int id;
    private String nome;
    private String categoria;
    private double preco;
    private int quantidade;

    public Item(int id, String nome, String categoria, double preco, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return String.format("%s (Categoria: %s, Preço: %.2f, Quantidade: %d)", nome, categoria, preco, quantidade);
    }
}