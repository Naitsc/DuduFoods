package com.naitsc.dudufoods.Model;

public class Produto {

    private int foto;
    private String nome;
    private String preco;
    private String descriacao;

    public Produto(int foto, String nome, String preco) {
        this.foto = foto;
        this.nome = nome;
        this.preco = preco;
    }

    public int getFoto() {
        return foto;
    }

    public void setFoto(int foto) {
        this.foto = foto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPreco() {
        return preco;
    }

    public void setPreco(String preco) {
        this.preco = preco;
    }

    public String getDescriacao() {
        return descriacao;
    }

    public void setDescriacao(String descriacao) {
        this.descriacao = descriacao;
    }
}
