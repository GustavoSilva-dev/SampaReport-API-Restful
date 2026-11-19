package br.com.fiap.sampareport.model;

import java.time.LocalDateTime;

public class Report {

    private Long id;
    private String titulo;
    private String descricao;
    private ReportType categoria;
    private Double latitude;
    private Double longitude;
    private String fotoAntesUrl;
    private ReportStatus status;
    private Integer quantidadeUpvotes;
    private LocalDateTime dataCriacao;
    
    private Long usuarioId;

    public Report() {
    }

    public Report(Long id, String titulo, String descricao, ReportType categoria,
                  Double latitude, Double longitude, String fotoAntesUrl,
                  ReportStatus status, Integer quantidadeUpvotes,
                  LocalDateTime dataCriacao, Long usuarioId) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fotoAntesUrl = fotoAntesUrl;
        this.status = status;
        this.quantidadeUpvotes = quantidadeUpvotes;
        this.dataCriacao = dataCriacao;
        this.usuarioId = usuarioId;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public ReportType getCategoria() { return categoria; }
    public void setCategoria(ReportType categoria) { this.categoria = categoria; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getFotoAntesUrl() { return fotoAntesUrl; }
    public void setFotoAntesUrl(String fotoAntesUrl) { this.fotoAntesUrl = fotoAntesUrl; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public Integer getQuantidadeUpvotes() { return quantidadeUpvotes; }
    public void setQuantidadeUpvotes(Integer quantidadeUpvotes) { this.quantidadeUpvotes = quantidadeUpvotes; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}