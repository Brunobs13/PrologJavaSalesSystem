import org.jpl7.Query;
import org.jpl7.Term;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;



public class Store {
    public List<Cliente> lerClientes() {
        List<Cliente> clientes = new ArrayList<>();
        Query query = new Query("cliente(ID, Nome, Distrito, AnosLealdade)");
        while (query.hasMoreSolutions()) {
            Map<String, Term> sol = query.nextSolution();
            int id = sol.get("ID").intValue();
            String nome = sol.get("Nome").name();
            String distrito = sol.get("Distrito").name();
            int anosLealdade = sol.get("AnosLealdade").intValue();
            clientes.add(new Cliente(id, nome, distrito, anosLealdade));
        }
        return clientes;
    }
    public boolean registrarVenda(int clienteId, String data, List<Item> itens) {
        Locale.setDefault(Locale.US);

        // Construir a lista de itens como uma string Prolog
        StringBuilder itensStr = new StringBuilder("[");
        for (Item item : itens) {
            itensStr.append(String.format("item(%d, '%s', '%s', %.2f, %d), ",
                    item.getId(), item.getNome(), item.getCategoria(), item.getPreco(), item.getQuantidade()));
        }
        // Remover a última vírgula e espaço e adicionar o fechamento da lista
        if (itens.size() > 0) {
            itensStr.setLength(itensStr.length() - 2);
        }
        itensStr.append("]");

        // Query Prolog para registrar a venda
        String queryStr = String.format(Locale.US, "registrar_venda(%d, '%s', %s)",
                                        clienteId, data, itensStr.toString());
        System.out.println("Query Prolog: " + queryStr);//depurar erro 
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao registrar venda: " + e.getMessage());
            return false;
        }
    }

    public double getItemTotalFromProlog(List<Item> items) {
        StringBuilder itemsTerm = new StringBuilder("[");
        for (Item item : items) {
            itemsTerm.append(String.format("item(%d, '%s', '%s', %.2f, %d), ",
                    item.getId(), item.getNome(), item.getCategoria(), item.getPreco(), item.getQuantidade()));
        }
        if (itemsTerm.length() > 1) {
            itemsTerm.setLength(itemsTerm.length() - 2);
        }
        itemsTerm.append("]");

        String queryStr = String.format("calcular_total_itens(%s, Total)", itemsTerm.toString());
        Query query = new Query(queryStr);
        if (query.hasSolution()) {
            return query.oneSolution().get("Total").doubleValue();
        }
        return 0.0;
    }

    public double getDiscountsFromProlog(List<Item> items, int anosLealdade) {
        StringBuilder itemsTerm = new StringBuilder("[");
        for (Item item : items) {
            itemsTerm.append(String.format("item(%d, '%s', '%s', %.2f, %d), ",
                    item.getId(), item.getNome(), item.getCategoria(), item.getPreco(), item.getQuantidade()));
        }
        if (itemsTerm.length() > 1) {
            itemsTerm.setLength(itemsTerm.length() - 2);
        }
        itemsTerm.append("]");

        String queryStr = String.format("calcular_descontos(%s, %d, TotalDescontos)", itemsTerm.toString(), anosLealdade);
        Query query = new Query(queryStr);
        if (query.hasSolution()) {
            return query.oneSolution().get("TotalDescontos").doubleValue();
        }
        return 0.0;
    }

    public double getShippingCostFromProlog(String distrito) {
        String queryStr = String.format("shipping_cost('%s', CustoEnvio)", distrito);
        Query query = new Query(queryStr);
        if (query.hasSolution()) {
            return query.oneSolution().get("CustoEnvio").doubleValue();
        }
        return 0.0;
    }

    

    public List<Item> lerItens() {
        List<Item> itens = new ArrayList<>();
        Query query = new Query("item(ID, Nome, Categoria, Custo, Quantidade)");
        while (query.hasMoreSolutions()) {
            Map<String, Term> sol = query.nextSolution();
            int id = sol.get("ID").intValue();
            String nome = sol.get("Nome").name();
            String categoria = sol.get("Categoria").name();
            double custo = sol.get("Custo").doubleValue();
            int quantidade = sol.get("Quantidade").intValue();
            itens.add(new Item(id, nome, categoria, custo, quantidade));
        }
        return itens;
    }

    public double getCategoryDiscount(String categoria) {
        Query query = new Query("discount('" + categoria + "', Desconto)");
        if (query.hasSolution()) {
            return query.oneSolution().get("Desconto").doubleValue();
        }
        return 0.0;
    }

    public double getLoyaltyDiscount(int anosLealdade) {
        Query query = new Query("loyalty_discount(" + anosLealdade + ", Desconto)");
        if (query.hasSolution()) {
            return query.oneSolution().get("Desconto").doubleValue();
        }
        return 0.0;
    }

    public double getShippingCost(String distrito) {
        Query query = new Query("shipping_cost('" + distrito + "', Custo)");
        if (query.hasSolution()) {
            return query.oneSolution().get("Custo").doubleValue();
        }
        return 0.0;
    }

    public void registrarCompra(Cart carrinho) {
        String data = java.time.LocalDate.now().toString(); // Substituir pela data atual
        double itemTotal = getItemTotalFromProlog(carrinho.getItems());
        double descontos = getDiscountsFromProlog(carrinho.getItems(), carrinho.getCliente().getAnosLealdade());
        double custoEnvio = getShippingCostFromProlog(carrinho.getCliente().getDistrito());
        double totalFinal = itemTotal - descontos + custoEnvio;

        String queryStr = String.format(
            "registrar_venda(%d, '%s', %.2f, %.2f, %.2f, %.2f, %.2f)",
            carrinho.getCliente().getId(), data, itemTotal, descontos, descontos, custoEnvio, totalFinal
        );

        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        query.hasSolution(); // Executar a consulta
    }

    public List<Map<String, Term>> getVendasPorData(String data) {
        List<Map<String, Term>> vendas = new ArrayList<>();
        String consulta = String.format(Locale.US, "findall(purchase(ClienteID, Data, Valor, DescCategoria, DescLealdade, CustoEnvio, Total), purchase(ClienteID, '%s', Valor, DescCategoria, DescLealdade, CustoEnvio, Total), Vendas)", data);
        Query query = new Query(consulta);
        if (query.hasSolution()) {
            Term vendasTerm = query.oneSolution().get("Vendas");
            if (vendasTerm != null && vendasTerm.isList()) {
                for (Term venda : vendasTerm.toTermArray()) {
                    Map<String, Term> vendaMap = new HashMap<>();
                    vendaMap.put("ClienteID", venda.arg(1));
                    vendaMap.put("Data", venda.arg(2));
                    vendaMap.put("Valor", venda.arg(3));
                    vendaMap.put("DescCategoria", venda.arg(4));
                    vendaMap.put("DescLealdade", venda.arg(5));
                    vendaMap.put("CustoEnvio", venda.arg(6));
                    vendaMap.put("Total", venda.arg(7));
                    vendas.add(vendaMap);
                }
            }
        }
        return vendas;
    }
    
    

    public List<Map<String, Term>> getVendasPorCliente(int clienteID) {
        List<Map<String, Term>> vendas = new ArrayList<>();
        Query query = new Query("vendas_por_cliente(" + clienteID + ", Data, Valor, DescCategoria, DescLealdade, CustoEnvio, Total)");
        while (query.hasMoreSolutions()) {
            vendas.add(query.nextSolution());
        }
        return vendas;
    }

    public List<Map<String, Term>> getVendasPorDistrito(String distrito) {
        List<Map<String, Term>> vendas = new ArrayList<>();
        Query query = new Query("vendas_por_distrito('" + distrito + "', ClienteID, Data, Valor, DescCategoria, DescLealdade, CustoEnvio, Total)");
        while (query.hasMoreSolutions()) {
            vendas.add(query.nextSolution());
        }
        return vendas;
    }

    public Map<String, Term> getTotaisPorDistrito(String distrito) {
        Query query = new Query("totais_por_distrito('" + distrito + "', TotalValor, TotalDescCategoria, TotalDescLealdade, TotalCustoEnvio, TotalFinal)");
        if (query.hasSolution()) {
            return query.oneSolution();
        }
        return null;
    }

    public Map<String, Term> getTotaisPorData(String data) {
        Locale.setDefault(Locale.US);

        // Formatar a consulta Prolog
        String consulta = String.format("totais_por_data('%s', TotalValor, TotalDescCategoria, TotalDescLealdade, TotalCustoEnvio, TotalFinal)", data);
        Query query = new Query(consulta);

        // Executar a consulta e verificar se há soluções
        if (query.hasSolution()) {
            Map<String, Term> resultado = new HashMap<>();
            Map<String, Term> solution = query.oneSolution();

            // Extrair cada termo da solução e adicionar ao mapa
            resultado.put("TotalValor", solution.get("TotalValor"));
            resultado.put("TotalDescCategoria", solution.get("TotalDescCategoria"));
            resultado.put("TotalDescLealdade", solution.get("TotalDescLealdade"));
            resultado.put("TotalCustoEnvio", solution.get("TotalCustoEnvio"));
            resultado.put("TotalFinal", solution.get("TotalFinal"));

            return resultado;
        }
        return null;
    }

    public String getDistritoMaisDescontos() {
        Query query = new Query("distrito_mais_descontos(Distrito)");
        if (query.hasSolution()) {
            return query.oneSolution().get("Distrito").name();
        }
        return null;
    }
    public List<Map<String, Term>> verItens() {
        List<Map<String, Term>> itens = new ArrayList<>();
        Query query = new Query("item(ID, Nome, Categoria, Custo, Quantidade)");
        while (query.hasMoreSolutions()) {
            itens.add(query.nextSolution());
        }
        return itens;
    }

    public List<Map<String, Term>> verItensCategoria(String categoria) {
        List<Map<String, Term>> itens = new ArrayList<>();
        Query query = new Query(String.format("item(ItemID, Nome, Categoria, Custo, Quantidade), Categoria == '%s'", categoria));
        while (query.hasMoreSolutions()) {
            itens.add(query.nextSolution());
        }
        return itens;
    }

    public List<String> verCategorias() {
        List<String> categorias = new ArrayList<>();
        Query query = new Query("findall(Categoria, discount(Categoria, _), Categorias)");
        if (query.hasSolution()) {
            Term categoriasTerm = query.oneSolution().get("Categorias");
            for (Term categoria : categoriasTerm.toTermArray()) {
                categorias.add(categoria.name());
            }
        }
        return categorias;
    }

    public boolean adicionarCategoria(String categoria, double desconto) {
        // Remover a categoria existente com o mesmo nome, se houver
        String removeQueryStr = String.format(Locale.US, "retractall(discount('%s', _))", categoria);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção
    
        // Adicionar a nova categoria
        String queryStr = String.format(Locale.US, "assertz(discount('%s', %.2f))", categoria, desconto);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao adicionar categoria: " + e.getMessage());
            return false;
        }
    }
    
    
    public boolean categoriaExiste(String categoria) {
        String queryStr = String.format("discount('%s', _)", categoria);
        Query query = new Query(queryStr);
        return query.hasSolution();
    }

    public boolean modificarCategoria(String categoria, double novoDesconto) {
        // Remover a categoria existente
        String removeQueryStr = String.format(Locale.US, "retractall(discount('%s', _))", categoria);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção
    
        // Adicionar a nova categoria com o valor atualizado
        String queryStr = String.format(Locale.US, "assertz(discount('%s', %.2f))", categoria, novoDesconto);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao modificar categoria: " + e.getMessage());
            return false;
        }
    }

    public boolean removerCategoria(String categoria) {
        Query query = new Query("remover_categoria('" + categoria + "')");
        return query.hasSolution();
    }

    public boolean adicionarItem(int id, String nome, String categoria, double custo, int quantidade) {
        // Remover o item existente com o mesmo ID, se houver
        String removeQueryStr = String.format(Locale.US, "retractall(item(%d, _, _, _, _))", id);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção
    
        // Adicionar o novo item
        String queryStr = String.format(Locale.US, "assertz(item(%d, '%s', '%s', %.2f, %d))", id, nome, categoria, custo, quantidade);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao adicionar item: " + e.getMessage());
            return false;
        }
    }
    


    public boolean modificarItem(int itemID, String novoNome, String novaCategoria, double novoCusto, int novaQuantidade) {
        // Remover o item existente com o mesmo ID
        String removeQueryStr = String.format(Locale.US, "retractall(item(%d, _, _, _, _))", itemID);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção
    
        // Adicionar o novo item com os valores atualizados
        String queryStr = String.format(Locale.US, "assertz(item(%d, '%s', '%s', %.2f, %d))", itemID, novoNome, novaCategoria, novoCusto, novaQuantidade);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao modificar item: " + e.getMessage());
            return false;
        }
    }
    

    public boolean removerItem(int id) {
        String queryStr = String.format("retractall(item(%d, _, _, _, _))", id);
        Query query = new Query(queryStr);
        return query.hasSolution();
    }

    public List<Map<String, Term>> verCustosEnvio() {
        List<Map<String, Term>> custos = new ArrayList<>();
        Query query = new Query("ver_custos_envio(Cidade, Custo)");
        while (query.hasMoreSolutions()) {
            custos.add(query.nextSolution());
        }
        return custos;
    }

    public List<Map<String, Term>> verDescontosCategoria() {
        List<Map<String, Term>> descontos = new ArrayList<>();
        Query query = new Query("ver_descontos_categoria(Categoria, Desconto)");
        while (query.hasMoreSolutions()) {
            descontos.add(query.nextSolution());
        }
        return descontos;
    }

    public List<Map<String, Term>> verDescontosLealdade() {
        Locale.setDefault(Locale.US);
        List<Map<String, Term>> resultados = new ArrayList<>();

        // Consulta para os descontos de lealdade usando between
        String consulta = "findall(loyalty_discount(Anos, Desconto), (between(1, 100, Anos), loyalty_discount(Anos, Desconto)), Descontos)";
        Query query = new Query(consulta);
        Map<String, Term> solucao = query.oneSolution();
        Term descontosTerm = solucao.get("Descontos");

        if (descontosTerm != null && descontosTerm.isList()) {
            for (Term desconto : descontosTerm.toTermArray()) {
                Map<String, Term> descontoMap = new HashMap<>();
                descontoMap.put("Anos", desconto.arg(1));
                descontoMap.put("Desconto", desconto.arg(2));
                resultados.add(descontoMap);
            }
        }

        return resultados;
    }

    public boolean adicionarCustoEnvio(String cidade, double custo) {
        // Remover o custo de envio existente para a mesma cidade, se houver
        String removeQueryStr = String.format(Locale.US, "retractall(shipping_cost('%s', _))", cidade);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção
    
        // Adicionar o novo custo de envio
        String queryStr = String.format(Locale.US, "assertz(shipping_cost('%s', %.2f))", cidade, custo);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao adicionar custo de envio: " + e.getMessage());
            return false;
        }
    }
    

    public boolean modificarCustoEnvio(String cidade, double novoCusto) {
        // Remover o custo de envio existente para a cidade
        String removeQueryStr = String.format(Locale.US, "retractall(shipping_cost('%s', _))", cidade);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção
    
        // Adicionar o novo custo de envio
        String queryStr = String.format(Locale.US, "assertz(shipping_cost('%s', %.2f))", cidade, novoCusto);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao modificar custo de envio: " + e.getMessage());
            return false;
        }
    }
    

    public boolean removerCustoEnvio(String cidade) {
        Query query = new Query("remover_custo_envio('" + cidade + "')");
        return query.hasSolution();
    }

    public boolean adicionarDescontoCategoria(String categoria, double desconto) {
        // Remover a categoria existente com o mesmo nome, se houver
        String removeQueryStr = String.format(Locale.US, "retractall(discount('%s', _))", categoria);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção
    
        // Adicionar a nova categoria
        String queryStr = String.format(Locale.US, "assertz(discount('%s', %.2f))", categoria, desconto);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao adicionar desconto de categoria: " + e.getMessage());
            return false;
        }
    }
    

    public boolean modificarDescontoCategoria(String categoria, double novoDesconto) {
        // Remover o desconto existente para a categoria
        String removeQueryStr = String.format(Locale.US, "retractall(discount('%s', _))", categoria);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção
    
        // Adicionar o novo desconto para a categoria
        String queryStr = String.format(Locale.US, "assertz(discount('%s', %.2f))", categoria, novoDesconto);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao modificar desconto de categoria: " + e.getMessage());
            return false;
        }
    }
    

    public boolean removerDescontoCategoria(String categoria) {
        Query query = new Query("remover_desconto_categoria('" + categoria + "')");
        return query.hasSolution();
    }

    public boolean adicionarDescontoLealdade(int anos, double desconto) {
        Locale.setDefault(Locale.US);

        // Remover o desconto existente, se houver
        String removeQueryStr = String.format(Locale.US, "retractall(loyalty_discount(%d, _))", anos);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção

        String queryStr = String.format(Locale.US, "assertz(loyalty_discount(%d, %.2f))", anos, desconto);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao adicionar desconto de lealdade: " + e.getMessage());
            return false;
        }
    }
    
    

    public boolean modificarDescontoLealdade(int anos, double novoDesconto) {
        // Remover o desconto de lealdade existente
        String removeQueryStr = String.format(Locale.US, "retractall(loyalty_discount(%d, _))", anos);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção
    
        // Adicionar o novo desconto de lealdade
        String queryStr = String.format(Locale.US, "assertz(loyalty_discount(%d, %.2f))", anos, novoDesconto);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao modificar desconto de lealdade: " + e.getMessage());
            return false;
        }
    }
    

    public boolean removerDescontoLealdade(int anos) {
        Query query = new Query("remover_desconto_lealdade(" + anos + ")");
        return query.hasSolution();
    }
    public List<Map<String, Term>> verTodosClientes() {
        List<Map<String, Term>> clientes = new ArrayList<>();
        Query query = new Query("findall(cliente(ID, Nome, Distrito, AnosLealdade), cliente(ID, Nome, Distrito, AnosLealdade), Clientes)");
        if (query.hasSolution()) {
            Term clientesTerm = query.oneSolution().get("Clientes");
            for (Term cliente : clientesTerm.toTermArray()) {
                Map<String, Term> clienteMap = new HashMap<>();
                clienteMap.put("ID", cliente.arg(1));
                clienteMap.put("Nome", cliente.arg(2));
                clienteMap.put("Distrito", cliente.arg(3));
                clienteMap.put("AnosLealdade", cliente.arg(4));
                clientes.add(clienteMap);
            }
        }
        return clientes;
    }

    public List<Map<String, Term>> verClientesDistrito(String distrito) {
        List<Map<String, Term>> clientes = new ArrayList<>();
        Query query = new Query("findall(cliente(ID, Nome, Distrito, AnosLealdade), (cliente(ID, Nome, Distrito, AnosLealdade), Distrito == '" + distrito + "'), Clientes)");
        if (query.hasSolution()) {
            Term clientesTerm = query.oneSolution().get("Clientes");
            for (Term cliente : clientesTerm.toTermArray()) {
                Map<String, Term> clienteMap = new HashMap<>();
                clienteMap.put("ID", cliente.arg(1));
                clienteMap.put("Nome", cliente.arg(2));
                clienteMap.put("Distrito", cliente.arg(3));
                clienteMap.put("AnosLealdade", cliente.arg(4));
                clientes.add(clienteMap);
            }
        }
        return clientes;
    }

    public List<Map<String, Term>> verClientesLealdade(int valor) {
        List<Map<String, Term>> clientes = new ArrayList<>();
        Query query = new Query("findall(cliente(ID, Nome, Distrito, AnosLealdade), (cliente(ID, Nome, Distrito, AnosLealdade), AnosLealdade > " + valor + "), Clientes)");
        if (query.hasSolution()) {
            Term clientesTerm = query.oneSolution().get("Clientes");
            for (Term cliente : clientesTerm.toTermArray()) {
                Map<String, Term> clienteMap = new HashMap<>();
                clienteMap.put("ID", cliente.arg(1));
                clienteMap.put("Nome", cliente.arg(2));
                clienteMap.put("Distrito", cliente.arg(3));
                clienteMap.put("AnosLealdade", cliente.arg(4));
                clientes.add(clienteMap);
            }
        }
        return clientes;
    }

    public boolean adicionarCliente(int id, String nome, String distrito, int anosLealdade) {
        // Remover o cliente existente com o mesmo ID, se houver
        String removeQueryStr = String.format(Locale.US, "retractall(cliente(%d, _, _, _))", id);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção
    
        // Adicionar o novo cliente
        String queryStr = String.format(Locale.US, "assertz(cliente(%d, '%s', '%s', %d))", id, nome, distrito, anosLealdade);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao adicionar cliente: " + e.getMessage());
            return false;
        }
    }
    

    public boolean modificarCliente(int id, String novoNome, String novoDistrito, int novosAnosLealdade) {
        // Remover o cliente existente
        String removeQueryStr = String.format(Locale.US, "retractall(cliente(%d, _, _, _))", id);
        Query removeQuery = new Query(removeQueryStr);
        removeQuery.hasSolution();  // Executa a remoção
    
        // Adicionar o novo cliente com as informações atualizadas
        String queryStr = String.format(Locale.US, "assertz(cliente(%d, '%s', '%s', %d))", id, novoNome, novoDistrito, novosAnosLealdade);
        System.out.println("Query Prolog: " + queryStr); // Depuração
        Query query = new Query(queryStr);
        try {
            return query.hasSolution();
        } catch (Exception e) {
            System.err.println("Erro ao modificar cliente: " + e.getMessage());
            return false;
        }
    }
    

    public boolean removerCliente(int id) {
        Query query = new Query("remover_cliente(" + id + ")");
        return query.hasSolution();
    }
    

}


