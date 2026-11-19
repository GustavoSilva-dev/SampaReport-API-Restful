package br.com.fiap.sampareport.model;

import java.time.LocalDateTime;

public class Usuario {

    private Long id;
    private String nome;
    private String email;
    private String senha;
    private RoleType perfil;
    private LocalDateTime dataCriacao;


    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, String senha, RoleType perfil, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.dataCriacao = dataCriacao;
    }


    public Usuario(String nome, String email, String senha, RoleType perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public RoleType getPerfil() {
        return perfil;
    }

    public void setPerfil(RoleType perfil) {
        this.perfil = perfil;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}