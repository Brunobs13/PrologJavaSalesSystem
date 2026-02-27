import org.jpl7.Query;
import org.jpl7.Term;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Locale;
import org.jpl7.*;
import java.util.*;
import java.lang.Integer;

public class Main {
    public static void main(String[] args) {
        // Inicializar Prolog
        String consult = "consult('/Users/brunoferreira/Downloads/EfolioBLinguagens de programação/store.pl')";
        Query q1 = new Query(consult);
        if (q1.hasSolution()) {
            System.out.println("Arquivo Prolog carregado com sucesso.");
        } else {
            System.out.println("Falha ao carregar o arquivo Prolog.");
            return;
        }

        Store store = new Store();
        Scanner scanner = new Scanner(System.in).useLocale(Locale.US);
        boolean exit = false;

        while (!exit) {
            System.out.println("Selecione uma opção:");
            System.out.println("1: Registrar uma nova venda");
            System.out.println("2: Histórico de Vendas");
            System.out.println("3: Gestão de Inventário");
            System.out.println("4: Gestão de Custos e Descontos");
            System.out.println("5: Gestão de Clientes");
            System.out.println("0: Sair");
            int option = readInt(scanner, "Selecione uma opção:");

            switch (option) {
                case 1:
                    registrarVenda(store, scanner);
                    break;
                case 2:
                    menuHistoricoVendas(store, scanner);
                    break;
                case 3:
                    menuInventario(store, scanner);
                    break;
                case 4:
                    menuCustosDescontos(store, scanner);
                    break;
                case 5:
                    menuClientes(store, scanner);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
        scanner.close();
    }

    private static void menuHistoricoVendas(Store store, Scanner scanner) {
        boolean exit = false;
        while (!exit) {
            System.out.println("Histórico de Vendas");
            System.out.println("1: Ver todas as vendas de uma determinada data");
            System.out.println("2: Ver todas as vendas de um determinado cliente");
            System.out.println("3: Ver todas as vendas de um determinado distrito");
            System.out.println("4: Ver os totais das vendas de um determinado distrito");
            System.out.println("5: Ver os totais das vendas de uma determinada data");
            System.out.println("6: Saber o distrito onde foram dados mais descontos");
            System.out.println("0: Voltar ao menu principal");

            int option = readInt(scanner, "Selecione uma opção:");

            switch (option) {
                case 1:
                    verVendasPorData(store, scanner);
                    break;
                case 2:
                    verVendasPorCliente(store, scanner);
                    break;
                case 3:
                    verVendasPorDistrito(store, scanner);
                    break;
                case 4:
                    verTotaisPorDistrito(store, scanner);
                    break;
                case 5:
                    verTotaisPorData(store, scanner);
                    break;
                case 6:
                    verDistritoMaisDescontos(store);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void menuInventario(Store store, Scanner scanner) {
        boolean exit = false;

        while (!exit) {
            System.out.println("Gestão de Inventário");
            System.out.println("1: Ver itens em inventário");
            System.out.println("2: Ver itens em inventário de uma determinada categoria");
            System.out.println("3: Ver categorias disponíveis");
            System.out.println("4: Adicionar uma categoria");
            System.out.println("5: Modificar uma categoria");
            System.out.println("6: Remover uma categoria");
            System.out.println("7: Adicionar um item de inventário");
            System.out.println("8: Modificar um item de inventário");
            System.out.println("9: Remover um item de inventário");
            System.out.println("0: Voltar ao menu principal");
            int option = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer

            switch (option) {
                case 1:
                verItens(store);
                    break;
                case 2:
                    verItensCategoria(store, scanner);
                    break;
                case 3:
                    verCategorias(store);
                    break;
                case 4:
                    adicionarCategoria(store, scanner);
                    break;
                case 5:
                    modificarCategoria(store, scanner);
                    break;
                case 6:
                    removerCategoria(store, scanner);
                    break;
                case 7:
                    adicionarItem(store, scanner);
                    break;
                case 8:
                    modificarItem(store, scanner);
                    break;
                case 9:
                    removerItem(store, scanner);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    


    private static void menuCustosDescontos(Store store, Scanner scanner) {
        boolean exit = false;

        while (!exit) {
            System.out.println("Gestão de Custos e Descontos:");
            System.out.println("1: Ver todos os custos de envio");
            System.out.println("2: Ver todos os tipos de desconto de categoria");
            System.out.println("3: Ver todos os tipos de desconto de lealdade");
            System.out.println("4: Adicionar um custo de envio");
            System.out.println("5: Modificar um custo de envio");
            System.out.println("6: Remover um custo de envio");
            System.out.println("7: Adicionar um desconto de categoria");
            System.out.println("8: Modificar um desconto de categoria");
            System.out.println("9: Remover um desconto de categoria");
            System.out.println("10: Adicionar um desconto de lealdade");
            System.out.println("11: Modificar um desconto de lealdade");
            System.out.println("12: Remover um desconto de lealdade");
            System.out.println("0: Voltar ao menu principal");
            int option = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer

            switch (option) {
                case 1:
                    verCustosEnvio(store);
                    break;
                case 2:
                    verDescontosCategoria(store);
                    break;
                case 3:
                    verDescontosLealdade(store);
                    break;
                case 4:
                    adicionarCustoEnvio(store, scanner);
                    break;
                case 5:
                    modificarCustoEnvio(store, scanner);
                    break;
                case 6:
                    removerCustoEnvio(store, scanner);
                    break;
                case 7:
                    adicionarDescontoCategoria(store, scanner);
                    break;
                case 8:
                    modificarDescontoCategoria(store, scanner);
                    break;
                case 9:
                    removerDescontoCategoria(store, scanner);
                    break;
                case 10:
                    adicionarDescontoLealdade(store, scanner);
                    break;
                case 11:
                    modificarDescontoLealdade(store, scanner);
                    break;
                case 12:
                    removerDescontoLealdade(store, scanner);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static void menuClientes(Store store, Scanner scanner) {
        boolean exit = false;

        while (!exit) {
            System.out.println("Gestão de Clientes:");
            System.out.println("1: Ver todos os clientes");
            System.out.println("2: Ver todos os clientes de um determinado distrito");
            System.out.println("3: Ver todas as vendas de um determinado cliente");
            System.out.println("4: Ver todos os clientes com lealdade superior a determinado valor");
            System.out.println("5: Adicionar cliente");
            System.out.println("6: Modificar cliente");
            System.out.println("7: Remover cliente");
            System.out.println("0: Voltar ao menu principal");
            int option = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer

            switch (option) {
                case 1:
                    verTodosClientes(store);
                    break;
                case 2:
                    verClientesDistrito(store, scanner);
                    break;
                case 3:
                    verVendasPorCliente(store, scanner);
                    break;
                case 4:
                    verClientesLealdade(store, scanner);
                    break;
                case 5:
                    adicionarCliente(store, scanner);
                    break;
                case 6:
                    modificarCliente(store, scanner);
                    break;
                case 7:
                    removerCliente(store, scanner);
                    break;
                case 0:
                    exit = true;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
    }

    private static int readInt(Scanner scanner, String prompt) {
        int number = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print(prompt);
            try {
                number = scanner.nextInt();
                valid = true;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
                scanner.next(); // Limpa a entrada inválida
            }
        }
        return number;
    }

    // Métodos para gestão de vendas

    private static void registrarVenda(Store store, Scanner scanner) {
        // Selecionar cliente
        List<Cliente> clientes = store.lerClientes();
        System.out.println("Selecione um cliente:");
        for (int i = 0; i < clientes.size(); i++) {
            Cliente cliente = clientes.get(i);
            System.out.printf("%d: %s (Distrito: %s, Anos de Lealdade: %d)%n", i + 1, cliente.getNome(), cliente.getDistrito(), cliente.getAnosLealdade());
        }
        int clienteIndex = readInt(scanner, "Digite o número do cliente:") - 1;

        Cliente clienteSelecionado = clientes.get(clienteIndex);

        // Criar carrinho de compras para o cliente selecionado
        Cart carrinho = new Cart(clienteSelecionado);

        // Ler itens do inventário e permitir seleção
        List<Item> itens = store.lerItens();
        while (true) {
            System.out.println("Selecione um item para adicionar ao carrinho (ou 0 para finalizar):");
            for (int i = 0; i < itens.size(); i++) {
                Item item = itens.get(i);
                System.out.printf("%d: %s (Categoria: %s, Preço: %.2f, Quantidade disponível: %d)%n", i + 1, item.getNome(), item.getCategoria(), item.getPreco(), item.getQuantidade());
            }
            int itemIndex = readInt(scanner, "Digite o número do item:") - 1;
            if (itemIndex == -1) break;
            
            Item itemSelecionado = itens.get(itemIndex);

            int quantidade = readInt(scanner, "Digite a quantidade:");

            // Adicionar item ao carrinho verificando o estoque
            carrinho.addItem(new Item(itemSelecionado.getId(), itemSelecionado.getNome(), itemSelecionado.getCategoria(), itemSelecionado.getPreco(), quantidade), itemSelecionado.getQuantidade());
        }

        // Exibir resumo do carrinho
        System.out.println("Resumo do carrinho:");
        System.out.println(carrinho);

        // Realizar a venda
        if (realizarVenda(store, carrinho)) {
            System.out.println("Venda registrada com sucesso.");
        } else {
            System.out.println("Falha ao registrar a venda.");
        }
    }

    private static boolean realizarVenda(Store store, Cart carrinho) {
        double itemTotal = store.getItemTotalFromProlog(carrinho.getItems());
        double descontos = store.getDiscountsFromProlog(carrinho.getItems(), carrinho.getCliente().getAnosLealdade());
        double custoEnvio = store.getShippingCostFromProlog(carrinho.getCliente().getDistrito());
        double totalFinal = itemTotal - descontos + custoEnvio;
        String data = java.time.LocalDate.now().toString();
    
        try {
            return store.registrarVenda(
                    carrinho.getCliente().getId(), data, carrinho.getItems());
        } catch (Exception e) {
            System.err.println("Erro ao realizar a venda: " + e.getMessage());
            return false;
        }
    }

    private static void verVendasPorData(Store store, Scanner scanner) {
        boolean primeiraTentativa = true;
        while (true) {
            if (!primeiraTentativa) {
                System.out.print("Digite a data (dd/MM/yyyy): ");
            }
            String data = scanner.nextLine();
    
            // Verificar o formato da data
            if (!data.matches("\\d{2}/\\d{2}/\\d{4}")) {
                if (!primeiraTentativa) {
                    System.out.println("Formato de data inválido. Por favor, insira a data no formato dd/MM/yyyy.");
                }
                primeiraTentativa = false;
                continue; // Solicitar a entrada da data novamente
            }
    
            // Consultar as vendas pela data
            List<Map<String, Term>> vendas = store.getVendasPorData(data);
            if (vendas != null && !vendas.isEmpty()) {
                for (Map<String, Term> venda : vendas) {
                    if (venda.get("ClienteID") instanceof org.jpl7.Integer) {
                        org.jpl7.Integer clienteId = (org.jpl7.Integer) venda.get("ClienteID");
                        System.out.printf("ClienteID: %s, Valor: %.2f, Desconto Categoria: %.2f, Desconto Lealdade: %.2f, Custo Envio: %.2f, Total: %.2f%n",
                                clienteId.intValue(),
                                venda.get("Valor").doubleValue(),
                                venda.get("DescCategoria").doubleValue(),
                                venda.get("DescLealdade").doubleValue(),
                                venda.get("CustoEnvio").doubleValue(),
                                venda.get("Total").doubleValue());
                    } else {
                        System.out.println("Nenhuma venda encontrada para esta data.");
                    }
                }
            } else {
                System.out.println("Nenhuma venda encontrada para esta data.");
            }
            break; // Sair do loop após a consulta
        }
    }
    
    private static void verVendasPorCliente(Store store, Scanner scanner) {
        System.out.print("Digite o ID do cliente: ");
        int clienteID = scanner.nextInt();
        scanner.nextLine();  // Consumir a quebra de linha
        List<Map<String, Term>> vendas = store.getVendasPorCliente(clienteID);
        if (!vendas.isEmpty()) {
            for (Map<String, Term> venda : vendas) {
                System.out.printf("Data: %s, Valor: %.2f, Desconto Categoria: %.2f, Desconto Lealdade: %.2f, Custo Envio: %.2f, Total: %.2f%n",
                        venda.get("Data").name(),
                        venda.get("Valor").doubleValue(),
                        venda.get("DescCategoria").doubleValue(),
                        venda.get("DescLealdade").doubleValue(),
                        venda.get("CustoEnvio").doubleValue(),
                        venda.get("Total").doubleValue());
            }
        } else {
            System.out.println("Nenhuma venda encontrada para este cliente.");
        }
    }


    private static void verVendasPorDistrito(Store store, Scanner scanner) {
        boolean primeiraTentativa = true;
        while (true) {
            if (!primeiraTentativa) {
                System.out.print("Digite o nome do distrito: ");
            }
            String distrito = scanner.nextLine();
    
            // Verificar se o nome do distrito foi inserido
            if (distrito.isEmpty()) {
                if (!primeiraTentativa) {
                    System.out.println("Nome do distrito inválido. Por favor, insira o nome do distrito.");
                }
                primeiraTentativa = false;
                continue; // Solicitar a entrada do distrito novamente
            }
    
            // Consultar as vendas pelo distrito
            List<Map<String, Term>> vendas = store.getVendasPorDistrito(distrito);
            if (vendas != null && !vendas.isEmpty()) {
                for (Map<String, Term> venda : vendas) {
                    if (venda.get("ClienteID") instanceof org.jpl7.Integer) {
                        org.jpl7.Integer clienteId = (org.jpl7.Integer) venda.get("ClienteID");
                        System.out.printf("ClienteID: %s, Valor: %.2f, Desconto Categoria: %.2f, Desconto Lealdade: %.2f, Custo Envio: %.2f, Total: %.2f%n",
                                clienteId.intValue(),
                                venda.get("Valor").doubleValue(),
                                venda.get("DescCategoria").doubleValue(),
                                venda.get("DescLealdade").doubleValue(),
                                venda.get("CustoEnvio").doubleValue(),
                                venda.get("Total").doubleValue());
                    } else {
                        System.out.println("Nenhuma venda encontrada para este distrito.");
                    }
                }
            } else {
                System.out.println("Nenhuma venda encontrada para este distrito.");
            }
            break; // Sair do loop após a consulta
        }
    }
    

    private static void verTotaisPorDistrito(Store store, Scanner scanner) {
        boolean primeiraTentativa = true;
        while (true) {
            if (!primeiraTentativa) {
                System.out.print("Digite o distrito: ");
            }
            String distrito = scanner.nextLine();
    
            // Verificar se o nome do distrito foi inserido
            if (distrito.isEmpty()) {
                if (!primeiraTentativa) {
                    System.out.println("Nome do distrito inválido. Por favor, insira o nome do distrito.");
                }
                primeiraTentativa = false;
                continue; // Solicitar a entrada do distrito novamente
            }
    
            // Consultar os totais pelo distrito
            Map<String, Term> totais = store.getTotaisPorDistrito(distrito);
            if (totais != null) {
                System.out.printf("Total Valor: %.2f, Total Desconto Categoria: %.2f, Total Desconto Lealdade: %.2f, Total Custo Envio: %.2f, Total Final: %.2f%n",
                        totais.get("TotalValor").doubleValue(),
                        totais.get("TotalDescCategoria").doubleValue(),
                        totais.get("TotalDescLealdade").doubleValue(),
                        totais.get("TotalCustoEnvio").doubleValue(),
                        totais.get("TotalFinal").doubleValue());
            } else {
                System.out.println("Nenhuma venda encontrada para este distrito.");
            }
            break; // Sair do loop após a consulta
        }
    }

    private static void verTotaisPorData(Store store, Scanner scanner) {
        boolean primeiraTentativa = true;
        while (true) {
            if (!primeiraTentativa) {
                System.out.print("Digite a data (dd/MM/yyyy): ");
            }
            String data = scanner.nextLine();
    
            // Verificar o formato da data
            if (!data.matches("\\d{2}/\\d{2}/\\d{4}")) {
                if (!primeiraTentativa) {
                    System.out.println("Formato de data inválido. Por favor, insira a data no formato dd/MM/yyyy.");
                }
                primeiraTentativa = false;
                continue; // Solicitar a entrada da data novamente
            }
    
            // Consultar os totais pela data
            Map<String, Term> totais = store.getTotaisPorData(data);
            if (totais != null) {
                System.out.printf("Total Valor: %.2f, Total Desconto Categoria: %.2f, Total Desconto Lealdade: %.2f, Total Custo Envio: %.2f, Total Final: %.2f%n",
                        totais.get("TotalValor").doubleValue(),
                        totais.get("TotalDescCategoria").doubleValue(),
                        totais.get("TotalDescLealdade").doubleValue(),
                        totais.get("TotalCustoEnvio").doubleValue(),
                        totais.get("TotalFinal").doubleValue());
            } else {
                System.out.println("Nenhuma venda encontrada para esta data.");
            }
            break; // Sair do loop após a consulta
        }
    }
    

    private static void verDistritoMaisDescontos(Store store) {
        String distrito = store.getDistritoMaisDescontos();
        if (distrito != null) {
            System.out.println("O distrito onde foram dados mais descontos é: " + distrito);
        } else {
            System.out.println("Nenhum desconto encontrado.");
        }
    }

    // Métodos para gestão de inventário

    private static void verItens(Store store) {
        List<Map<String, Term>> itens = store.verItens();
        for (Map<String, Term> item : itens) {
            System.out.printf("ID: %d, Nome: %s, Categoria: %s, Custo: %.2f, Quantidade: %d%n",
                    item.get("ID").intValue(),
                    item.get("Nome").name(),
                    item.get("Categoria").name(),
                    item.get("Custo").doubleValue(),
                    item.get("Quantidade").intValue());
        }
    }


    private static void verItensCategoria(Store store, Scanner scanner) {
        System.out.print("Digite a categoria: ");
        String categoria = scanner.nextLine();
    
        List<Map<String, Term>> itens = store.verItensCategoria(categoria);
        if (itens == null || itens.isEmpty()) {
            System.out.println("Nenhum item encontrado para a categoria: " + categoria);
        } else {
            for (Map<String, Term> item : itens) {
                if (item != null && item.get("ItemID") != null) {
                    System.out.printf("ID: %d, Nome: %s, Custo: %.2f, Quantidade: %d%n",
                            item.get("ItemID").intValue(),
                            item.get("Nome").name(),
                            item.get("Custo").doubleValue(),
                            item.get("Quantidade").intValue());
                } else {
                    System.out.println("Erro ao obter os detalhes do item.");
                }
            }
        }
    }

    private static void verCategorias(Store store) {
        List<String> categorias = store.verCategorias();
        if (categorias == null || categorias.isEmpty()) {
            System.out.println("Nenhuma categoria encontrada.");
            return;
        }
    
        for (String categoria : categorias) {
            System.out.printf("Categoria: %s%n", categoria);
        }
    }
    

    private static void adicionarCategoria(Store store, Scanner scanner) {
        try {
            System.out.print("Digite a nova categoria: ");
            String categoria = scanner.nextLine();
    
            // Verificar se a categoria já existe
            if (store.categoriaExiste(categoria)) {
                System.out.println("Erro: A categoria já existe. Por favor, escolha uma categoria diferente.");
                return;
            }
    
            double desconto = 0.0;
            boolean validInput = false;
            while (!validInput) {
                try {
                    System.out.print("Digite o desconto para a nova categoria: ");
                    desconto = scanner.nextDouble();
                    scanner.nextLine(); // Limpar o buffer
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida. Por favor, insira um número para o desconto.");
                    scanner.nextLine(); // Limpar o buffer
                }
            }
    
            if (store.adicionarCategoria(categoria, desconto)) {
                System.out.println("Categoria adicionada com sucesso.");
               // verCategorias(store);  // Mostrar as categorias atualizadas
            } else {
                System.out.println("Falha ao adicionar a categoria.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar o buffer após entrada inválida
        }
    }

    private static void modificarCategoria(Store store, Scanner scanner) {
        try {
            System.out.print("Digite o nome da categoria a ser modificada: ");
            String categoria = scanner.nextLine();
    
            double novoDesconto = 0.0;
            boolean validInput = false;
            while (!validInput) {
                try {
                    System.out.print("Digite o novo valor do desconto: ");
                    novoDesconto = scanner.nextDouble();
                    scanner.nextLine(); // Limpar o buffer
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida. Por favor, insira um número para o desconto.");
                    scanner.nextLine(); // Limpar o buffer
                }
            }
    
            if (store.modificarCategoria(categoria, novoDesconto)) {
                System.out.println("Categoria modificada com sucesso.");
            } else {
                System.out.println("Falha ao modificar a categoria.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar o buffer após entrada inválida
        }
    }

    private static void removerCategoria(Store store, Scanner scanner) {
        System.out.print("Digite a categoria a ser removida: ");
        String categoria = scanner.nextLine();
        if (store.removerCategoria(categoria)) {
            System.out.println("Categoria removida com sucesso.");
        } else {
            System.out.println("Falha ao remover a categoria.");
        }
    }

    private static void adicionarItem(Store store, Scanner scanner) {
        try {
            System.out.print("Digite o ID do novo item: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer
    
            System.out.print("Digite o nome do novo item: ");
            String nome = scanner.nextLine();
    
            System.out.print("Digite a categoria do novo item: ");
            String categoria = scanner.nextLine();
    
            double custo = 0.0;
            boolean validInput = false;
            while (!validInput) {
                try {
                    System.out.print("Digite o custo do novo item: ");
                    custo = scanner.nextDouble();
                    scanner.nextLine(); // Limpar o buffer
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida. Por favor, insira um número para o custo.");
                    scanner.nextLine(); // Limpar o buffer
                }
            }
    
            System.out.print("Digite a quantidade do novo item: ");
            int quantidade = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer
    
            if (store.adicionarItem(id, nome, categoria, custo, quantidade)) {
                System.out.println("Item adicionado com sucesso.");
                verItens(store);  // Exibir o inventário atualizado
            } else {
                System.out.println("Falha ao adicionar o item.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar o buffer após entrada inválida
        }
    }
    

    private static void modificarItem(Store store, Scanner scanner) {
        try {
            System.out.print("Digite o ID do item a ser modificado: ");
            int itemID = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer
    
            System.out.print("Digite o novo nome do item: ");
            String novoNome = scanner.nextLine();
    
            System.out.print("Digite a nova categoria do item: ");
            String novaCategoria = scanner.nextLine();
    
            double novoCusto = 0.0;
            boolean validInput = false;
            while (!validInput) {
                try {
                    System.out.print("Digite o novo custo do item: ");
                    novoCusto = scanner.nextDouble();
                    scanner.nextLine(); // Limpar o buffer
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida. Por favor, insira um número para o custo.");
                    scanner.nextLine(); // Limpar o buffer
                }
            }
    
            System.out.print("Digite a nova quantidade do item: ");
            int novaQuantidade = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer
    
            if (store.modificarItem(itemID, novoNome, novaCategoria, novoCusto, novaQuantidade)) {
                System.out.println("Item modificado com sucesso.");
                // Mostrar os itens atualizados
                List<Map<String, Term>> itensAtualizados = store.verItens();
                for (Map<String, Term> item : itensAtualizados) {
                    System.out.printf("ID: %d, Nome: %s, Categoria: %s, Custo: %.2f, Quantidade: %d%n",
                        item.get("ID").intValue(),
                        item.get("Nome").name(),
                        item.get("Categoria").name(),
                        item.get("Custo").doubleValue(),
                        item.get("Quantidade").intValue());
                }
            } else {
                System.out.println("Falha ao modificar o item.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar o buffer após entrada inválida
        }
    }
    

    private static void removerItem(Store store, Scanner scanner) {
        System.out.print("Digite o ID do item a ser removido: ");
        int itemID = scanner.nextInt();
        scanner.nextLine(); // Limpar o buffer do scanner
        if (store.removerItem(itemID)) {
            System.out.println("Item removido com sucesso.");
        } else {
            System.out.println("Falha ao remover o item.");
        }
    }

    // Métodos para gestão de custos e descontos

    private static void verCustosEnvio(Store store) {
        List<Map<String, Term>> custos = store.verCustosEnvio();
        for (Map<String, Term> custo : custos) {
            System.out.printf("Cidade: %s, Custo: %.2f%n",
                custo.get("Cidade").name(),
                custo.get("Custo").doubleValue());
        }
    }

    private static void verDescontosCategoria(Store store) {
        List<Map<String, Term>> descontos = store.verDescontosCategoria();
        for (Map<String, Term> desconto : descontos) {
            System.out.printf("Categoria: %s, Desconto: %.2f%n",
                desconto.get("Categoria").name(),
                desconto.get("Desconto").doubleValue());
        }
    }

    public static void verDescontosLealdade(Store store) {
        List<Map<String, Term>> descontos = store.verDescontosLealdade();
        if (descontos.isEmpty()) {
            System.out.println("Nenhum desconto de lealdade encontrado.");
        } else {
            for (Map<String, Term> desconto : descontos) {
                int anos = desconto.get("Anos").intValue();
                double valorDesconto = desconto.get("Desconto").doubleValue();
                System.out.printf(Locale.US, "Anos de Lealdade: %d, Desconto: %.2f%%\n", anos, valorDesconto * 100);
            }
        }
    }
    

    private static void adicionarCustoEnvio(Store store, Scanner scanner) {
        try {
            System.out.print("Digite o nome da cidade: ");
            String cidade = scanner.nextLine();
    
            // Verificar se o custo de envio para a cidade já existe
            List<Map<String, Term>> custosExistentes = store.verCustosEnvio();
            for (Map<String, Term> custo : custosExistentes) {
                if (custo.get("Cidade").name().equalsIgnoreCase(cidade)) {
                    System.out.println("Erro: Já existe um custo de envio para esta cidade. Por favor, escolha uma cidade diferente ou modifique o custo existente.");
                    return;
                }
            }
    
            double custo = 0.0;
            boolean validInput = false;
            while (!validInput) {
                try {
                    System.out.print("Digite o custo de envio: ");
                    custo = scanner.nextDouble();
                    scanner.nextLine(); // Limpar o buffer
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida. Por favor, insira um número para o custo.");
                    scanner.nextLine(); // Limpar o buffer
                }
            }
    
            if (store.adicionarCustoEnvio(cidade, custo)) {
                System.out.println("Custo de envio adicionado com sucesso.");
                // Mostrar os custos de envio atualizados
                List<Map<String, Term>> custosAtualizados = store.verCustosEnvio();
                for (Map<String, Term> custoEnvio : custosAtualizados) {
                    System.out.printf("Cidade: %s, Custo: %.2f%n",
                        custoEnvio.get("Cidade").name(),
                        custoEnvio.get("Custo").doubleValue());
                }
            } else {
                System.out.println("Falha ao adicionar o custo de envio.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar o buffer após entrada inválida
        }
    }
    

    private static void modificarCustoEnvio(Store store, Scanner scanner) {
    try {
        System.out.print("Digite o nome da cidade: ");
        String cidade = scanner.nextLine();

        double novoCusto = 0.0;
        boolean validInput = false;
        while (!validInput) {
            try {
                System.out.print("Digite o novo custo de envio: ");
                novoCusto = scanner.nextDouble();
                scanner.nextLine(); // Limpar o buffer
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Por favor, insira um número para o custo.");
                scanner.nextLine(); // Limpar o buffer
            }
        }

        if (store.modificarCustoEnvio(cidade, novoCusto)) {
            System.out.println("Custo de envio modificado com sucesso.");
            // Mostrar os custos de envio atualizados
            List<Map<String, Term>> custosAtualizados = store.verCustosEnvio();
            for (Map<String, Term> custoEnvio : custosAtualizados) {
                System.out.printf("Cidade: %s, Custo: %.2f%n",
                    custoEnvio.get("Cidade").name(),
                    custoEnvio.get("Custo").doubleValue());
            }
        } else {
            System.out.println("Falha ao modificar o custo de envio.");
        }
    } catch (InputMismatchException e) {
        System.out.println("Entrada inválida. Por favor, insira os dados corretamente.");
        scanner.nextLine(); // Limpar o buffer após entrada inválida
    }
}


    private static void removerCustoEnvio(Store store, Scanner scanner) {
        System.out.print("Digite a cidade: ");
        String cidade = scanner.nextLine();
        if (store.removerCustoEnvio(cidade)) {
            System.out.println("Custo de envio removido com sucesso.");
        } else {
            System.out.println("Falha ao remover o custo de envio.");
        }
    }

    private static void adicionarDescontoCategoria(Store store, Scanner scanner) {
        try {
            System.out.print("Digite a categoria: ");
            String categoria = scanner.nextLine();
    
            double desconto = 0.0;
            boolean validInput = false;
            while (!validInput) {
                try {
                    System.out.print("Digite o desconto: ");
                    String descontoInput = scanner.nextLine().replace(",", ".");
                    desconto = Double.parseDouble(descontoInput);
                    validInput = true;
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Por favor, insira um número para o desconto.");
                }
            }
    
            if (store.adicionarDescontoCategoria(categoria, desconto)) {
                System.out.println("Desconto de categoria adicionado com sucesso.");
                // Mostrar os descontos de categoria atualizados
                List<Map<String, Term>> descontosCategoria = store.verDescontosCategoria();
                for (Map<String, Term> descontoCat : descontosCategoria) {
                    System.out.printf("Categoria: %s, Desconto: %.2f%n", descontoCat.get("Categoria").name(), descontoCat.get("Desconto").doubleValue());
                }
            } else {
                System.out.println("Falha ao adicionar o desconto de categoria.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar o buffer após entrada inválida
        }
    }

    private static void modificarDescontoCategoria(Store store, Scanner scanner) {
        try {
            System.out.print("Digite o nome da categoria a ser modificada: ");
            String categoria = scanner.nextLine();
    
            double novoDesconto = 0.0;
            boolean validInput = false;
            while (!validInput) {
                try {
                    System.out.print("Digite o novo valor do desconto: ");
                    novoDesconto = scanner.nextDouble();
                    scanner.nextLine(); // Limpar o buffer
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida. Por favor, insira um número para o desconto.");
                    scanner.nextLine(); // Limpar o buffer
                }
            }
    
            if (store.modificarDescontoCategoria(categoria, novoDesconto)) {
                System.out.println("Desconto de categoria modificado com sucesso.");
                // Mostrar os descontos de categoria atualizados
                List<Map<String, Term>> descontosAtualizados = store.verDescontosCategoria();
                for (Map<String, Term> desconto : descontosAtualizados) {
                    System.out.printf("Categoria: %s, Desconto: %.2f%n",
                        desconto.get("Categoria").name(),
                        desconto.get("Desconto").doubleValue());
                }
            } else {
                System.out.println("Falha ao modificar o desconto de categoria.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar o buffer após entrada inválida
        }
    }

    private static void removerDescontoCategoria(Store store, Scanner scanner) {
        System.out.print("Digite a categoria: ");
        String categoria = scanner.nextLine();
        if (store.removerDescontoCategoria(categoria)) {
            System.out.println("Desconto de categoria removido com sucesso.");
        } else {
            System.out.println("Falha ao remover o desconto de categoria.");
        }
    }

    private static void adicionarDescontoLealdade(Store store, Scanner scanner) {
        try {
            System.out.print("Digite os anos de lealdade: ");
            int anos = scanner.nextInt();
            System.out.print("Digite o valor do desconto: ");
            if (scanner.hasNextDouble()) {
                double desconto = scanner.nextDouble();
                boolean sucesso = store.adicionarDescontoLealdade(anos, desconto);
                if (sucesso) {
                    System.out.println("Desconto de lealdade adicionado com sucesso.");
                } else {
                    System.out.println("Falha ao adicionar desconto de lealdade.");
                }
            } else {
                throw new InputMismatchException();
            }
        } catch (InputMismatchException e) {
            System.out.println("Erro: Por favor, insira o valor do desconto usando um ponto (.) como separador decimal, em vez de uma vírgula (,).");
            scanner.next(); // Limpa a entrada inválida
        }
    }
    
    

    private static void modificarDescontoLealdade(Store store, Scanner scanner) {
        try {
            System.out.print("Digite o número de anos de lealdade a ser modificado: ");
            int anos = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer
    
            double novoDesconto = 0.0;
            boolean validInput = false;
            while (!validInput) {
                try {
                    System.out.print("Digite o novo valor do desconto: ");
                    novoDesconto = scanner.nextDouble();
                    scanner.nextLine(); // Limpar o buffer
                    validInput = true;
                } catch (InputMismatchException e) {
                    System.out.println("Entrada inválida. Por favor, insira um número para o desconto.");
                    scanner.nextLine(); // Limpar o buffer
                }
            }
    
            if (store.modificarDescontoLealdade(anos, novoDesconto)) {
                System.out.println("Desconto de lealdade modificado com sucesso.");
                // Mostrar os descontos de lealdade atualizados
                List<Map<String, Term>> descontosAtualizados = store.verDescontosLealdade();
                for (Map<String, Term> desconto : descontosAtualizados) {
                    System.out.printf("Anos: %d, Desconto: %.2f%n",
                        desconto.get("Anos").intValue(),
                        desconto.get("Desconto").doubleValue());
                }
            } else {
                System.out.println("Falha ao modificar o desconto de lealdade.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar o buffer após entrada inválida
        }
    }
    

    private static void removerDescontoLealdade(Store store, Scanner scanner) {
        System.out.print("Digite os anos de lealdade: ");
        int anos = scanner.nextInt();
        scanner.nextLine(); // Limpar o buffer do scanner
        if (store.removerDescontoLealdade(anos)) {
            System.out.println("Desconto de lealdade removido com sucesso.");
        } else {
            System.out.println("Falha ao remover o desconto de lealdade.");
        }
    }

    // Métodos para gestão de clientes

    private static void verTodosClientes(Store store) {
        List<Map<String, Term>> clientes = store.verTodosClientes();
        if (clientes == null || clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado.");
            return;
        }
    
        for (Map<String, Term> cliente : clientes) {
            java.lang.Integer id = cliente.containsKey("ID") ? cliente.get("ID").intValue() : null;
            String nome = cliente.containsKey("Nome") ? cliente.get("Nome").name() : "Desconhecido";
            String distrito = cliente.containsKey("Distrito") ? cliente.get("Distrito").name() : "Desconhecido";
            java.lang.Integer anosLealdade = cliente.containsKey("AnosLealdade") ? cliente.get("AnosLealdade").intValue() : null;
    
            if (id != null && anosLealdade != null) {
                System.out.printf("ID: %d, Nome: %s, Distrito: %s, Anos de Lealdade: %d%n", id, nome, distrito, anosLealdade);
            } else {
                System.out.println("Informação do cliente incompleta.");
            }
        }
    }

    private static void verClientesDistrito(Store store, Scanner scanner) {
        System.out.print("Digite o distrito: ");
        String distrito = scanner.nextLine();
        List<Map<String, Term>> clientes = store.verClientesDistrito(distrito);
        if (clientes == null || clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado para o distrito: " + distrito);
            return;
        }
    
        for (Map<String, Term> cliente : clientes) {
            java.lang.Integer id = cliente.containsKey("ID") ? cliente.get("ID").intValue() : null;
            String nome = cliente.containsKey("Nome") ? cliente.get("Nome").name() : "Desconhecido";
            String distritoCliente = cliente.containsKey("Distrito") ? cliente.get("Distrito").name() : "Desconhecido";
            java.lang.Integer anosLealdade = cliente.containsKey("AnosLealdade") ? cliente.get("AnosLealdade").intValue() : null;
    
            if (id != null && anosLealdade != null) {
                System.out.printf("ID: %d, Nome: %s, Distrito: %s, Anos de Lealdade: %d%n", id, nome, distritoCliente, anosLealdade);
            } else {
                System.out.println("Informação do cliente incompleta.");
            }
        }
    }

    private static void verClientesLealdade(Store store, Scanner scanner) {
        System.out.print("Digite o valor mínimo de anos de lealdade: ");
        int valor = scanner.nextInt();
        scanner.nextLine(); // Limpar o buffer do scanner
        List<Map<String, Term>> clientes = store.verClientesLealdade(valor);
        if (clientes == null || clientes.isEmpty()) {
            System.out.println("Nenhum cliente encontrado com anos de lealdade superior a: " + valor);
            return;
        }
    
        for (Map<String, Term> cliente : clientes) {
            java.lang.Integer id = cliente.containsKey("ID") ? cliente.get("ID").intValue() : null;
            String nome = cliente.containsKey("Nome") ? cliente.get("Nome").name() : "Desconhecido";
            String distrito = cliente.containsKey("Distrito") ? cliente.get("Distrito").name() : "Desconhecido";
            java.lang.Integer anosLealdade = cliente.containsKey("AnosLealdade") ? cliente.get("AnosLealdade").intValue() : null;
    
            if (id != null && anosLealdade != null) {
                System.out.printf("ID: %d, Nome: %s, Distrito: %s, Anos de Lealdade: %d%n", id, nome, distrito, anosLealdade);
            } else {
                System.out.println("Informação do cliente incompleta.");
            }
        }
    }

    private static void adicionarCliente(Store store, Scanner scanner) {
        try {
            System.out.print("Digite o ID do novo cliente: ");
            int clienteID = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer
    
            // Verificar se o ID já existe
            List<Map<String, Term>> clientesExistentes = store.verTodosClientes();
            for (Map<String, Term> cliente : clientesExistentes) {
                if (cliente.get("ID").intValue() == clienteID) {
                    System.out.println("Erro: Já existe um cliente com este ID. Por favor, escolha um ID diferente.");
                    return;
                }
            }
    
            System.out.print("Digite o nome do novo cliente: ");
            String nome = scanner.nextLine();
    
            System.out.print("Digite o distrito do novo cliente: ");
            String distrito = scanner.nextLine();
    
            System.out.print("Digite os anos de lealdade do novo cliente: ");
            int anosLealdade = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer
    
            if (store.adicionarCliente(clienteID, nome, distrito, anosLealdade)) {
                System.out.println("Cliente adicionado com sucesso.");
                // Mostrar os clientes atualizados
                List<Map<String, Term>> clientesAtualizados = store.verTodosClientes();
                for (Map<String, Term> cliente : clientesAtualizados) {
                    System.out.printf("ID: %d, Nome: %s, Distrito: %s, Anos de Lealdade: %d%n",
                        cliente.get("ID").intValue(),
                        cliente.get("Nome").name(),
                        cliente.get("Distrito").name(),
                        cliente.get("AnosLealdade").intValue());
                }
            } else {
                System.out.println("Falha ao adicionar o cliente.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar o buffer após entrada inválida
        }
    }
    

    private static void modificarCliente(Store store, Scanner scanner) {
        try {
            System.out.print("Digite o ID do cliente a ser modificado: ");
            int id = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer
    
            System.out.print("Digite o novo nome do cliente: ");
            String novoNome = scanner.nextLine();
    
            System.out.print("Digite o novo distrito do cliente: ");
            String novoDistrito = scanner.nextLine();
    
            System.out.print("Digite os novos anos de lealdade do cliente: ");
            int novosAnosLealdade = scanner.nextInt();
            scanner.nextLine(); // Limpar o buffer
    
            if (store.modificarCliente(id, novoNome, novoDistrito, novosAnosLealdade)) {
                System.out.println("Cliente modificado com sucesso.");
                // Mostrar os clientes atualizados
                List<Map<String, Term>> clientesAtualizados = store.verTodosClientes();
                for (Map<String, Term> cliente : clientesAtualizados) {
                    System.out.printf("ID: %d, Nome: %s, Distrito: %s, Anos de Lealdade: %d%n",
                        cliente.get("ID").intValue(),
                        cliente.get("Nome").name(),
                        cliente.get("Distrito").name(),
                        cliente.get("AnosLealdade").intValue());
                }
            } else {
                System.out.println("Falha ao modificar o cliente.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira os dados corretamente.");
            scanner.nextLine(); // Limpar o buffer após entrada inválida
        }
    }
    

    private static void removerCliente(Store store, Scanner scanner) {
        System.out.print("Digite o ID do cliente a ser removido: ");
        int clienteID = scanner.nextInt();
        scanner.nextLine(); // Limpar o buffer
        if (store.removerCliente(clienteID)) {
            System.out.println("Cliente removido com sucesso.");
        } else {
            System.out.println("Falha ao remover o cliente.");
        }
    }
}
