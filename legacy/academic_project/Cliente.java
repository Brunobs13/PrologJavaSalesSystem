import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;


public class Cliente {
    private int id;
    private String nome;
    private String distrito;
    private int anosLealdade;

    public Cliente(int id, String nome, String distrito, int anosLealdade) {
        this.id = id;
        this.nome = nome;
        this.distrito = distrito;
        this.anosLealdade = anosLealdade;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDistrito() {
        return distrito;
    }

    public int getAnosLealdade() {
        return anosLealdade;
    }
}
