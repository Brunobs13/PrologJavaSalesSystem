private static void verItensCategoria(Store store, Scanner scanner) {
        System.out.print("Digite a categoria: ");
        String categoria = scanner.nextLine();
        
        List<String> categorias = store.verCategorias();
        if (!categorias.contains(categoria)) {
            System.out.println("Insira o nome da categoria conforme consta na base de dados ou com letras minusculas");
            return;
        }

        List<Map<String, Term>> itens = store.verItensCategoria(categoria);
        for (Map<String, Term> item : itens) {
            System.out.printf("ID: %d, Nome: %s, Custo: %.2f, Quantidade: %d%n",
                item.get("ItemID").intValue(),
                item.get("Nome").name(),
                item.get("Custo").doubleValue(),
                item.get("Quantidade").intValue());
        }
    }