import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;



//public class OperacoesCarrinho {
    
    // Metodo privado para executar comandos OCaml e retornar a saída
    private static List<String> executarOCaml(String comando) throws IOException {
            // Primeiro passo e compilar o Ocaml
            ProcessBuilder pbCompile = new ProcessBuilder("ocamlc", "-o", "main", "str.cma", "main.ml");
            Process pCompile = pbCompile.start();
            try {
                pCompile.waitFor();  // Aguarde até que a compilação esteja concluída
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        
            // aqui executamos ja o codigo compilado
            ProcessBuilder pbRun = new ProcessBuilder("./main");
            Process pRun = pbRun.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(pRun.getInputStream()));
            List<String> saida = new ArrayList<>();
            String linha;
        
            while ((linha = reader.readLine()) != null) {
                saida.add(linha);
            }
        
            reader.close();
            return saida;
        }
    
    
    // Calculos .... calcular o preço total
    public static double calcularPrecoTotal(List<Item> itens) {
        double total = 0.0;
        for (Item item : itens) {
            String str = item.toString();
            try {
                String comando = "ocaml preco_total.ml " + str;
                List<String> saida = executarOCaml(comando);
                for (String linha : saida) {
                    if (linha.startsWith("Preço total sem descontos: ")) {
                        total = Double.parseDouble(linha.substring("Preço total sem descontos: ".length()));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return total;
    }
    
    // calcular descontos por categoria
    public static double calcularDescontoCategoria(List<Item> itens) {
        double total = 0.0;
        for (Item item : itens) {
            String str = item.toString();
            try {
                String comando = "ocaml desconto_categoria.ml " + str;
                List<String> saida = executarOCaml(comando);
                for (String linha : saida) {
                    if (linha.startsWith("Descontos por categoria: ")) {
                        total = Double.parseDouble(linha.substring("Descontos por categoria: ".length()));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return total;
    }
    
    //  calcular descontos de lealdade
    public static double calcularDescontoLealdade(String anosLealdade, String totalComDescontos) {
        double desconto = 0.0;
        try {
            String comando = "ocaml desconto_lealdade.ml " + anosLealdade + " " + totalComDescontos;
            List<String> saida = executarOCaml(comando);
            for (String linha : saida) {
                if (linha.startsWith("Desconto de lealdade: ")) {
                    desconto = Double.parseDouble(linha.substring("Desconto de lealdade: ".length()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return desconto;
    }
    
    
    // calcular custos de envio
    public static double calcularCustoEnvio(String distrito) {
        double custo = 0.0;
        try {
            String comando = "ocaml custo_envio.ml " + distrito;
            List<String> saida = executarOCaml(comando);
            for (String linha : saida) {
                if (linha.startsWith("Custo de envio: ")) {
                    custo = Double.parseDouble(linha.substring("Custo de envio: ".length()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return custo;
    }    
    // para calcular o preço final do carrinho
    public static double calcularPrecoFinal(double precoTotal, double descontoCategoria, double descontoLealdade, double custoEnvio) {
        double precoFinal = 0.0;
        try {
            String comando = "ocaml final_price.ml " + precoTotal + " " + descontoCategoria + " " + descontoLealdade + " " + custoEnvio;
            List<String> saida = executarOCaml(comando);
            for (String linha : saida) {
                if (linha.startsWith("Preço final: ")) {
                    precoFinal = Double.parseDouble(linha.substring("Preço final: ".length()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return precoFinal;
    }
    
    
    // exibir o carrinho de compras
    public static String exibirCarrinho(String carrinho) {
        StringBuilder carrinhoExibido = new StringBuilder();
        try {
            String comando = "ocaml exibir_carrinho.ml " + carrinho;
            List<String> saida = executarOCaml(comando);
            for (String linha : saida) {
                if (linha.startsWith("Item: ")) {
                    carrinhoExibido.append(linha).append("\n");
                }
            }
            if (carrinhoExibido.length() == 0) {
                return "O carrinho está vazio.";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return carrinhoExibido.toString();
    }
    
}

