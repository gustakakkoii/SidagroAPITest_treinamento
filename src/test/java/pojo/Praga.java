package pojo;

public class Praga {
    private String nomePopular;
    private String nomeCientifico;
    private String status;

    public Praga(String nomePopular, String nomeCientifico, String status){
        this.nomePopular = nomePopular;
        this.nomeCientifico = nomeCientifico;
        this.status = status;
    }

    public String getNomePopular(){return this.nomePopular;}
    public String getNomeCientifico(){return this.nomeCientifico;}
    public String getStatus(){return this.status;}
    public void setNomePopular(String nomePopular){this.nomePopular = nomePopular;}
    public void setNomeCientifico(String nomeCientifico){this.nomeCientifico = nomeCientifico;}
    public void setStatus(String status){this.status = status;}
}
