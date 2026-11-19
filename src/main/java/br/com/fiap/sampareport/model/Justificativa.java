package br.com.fiap.sampareport.model;

import java.time.LocalDateTime;

public class Justificativa {

    private Long id;
    private String fotoDepoisUrl;
    private String notaTecnica;
    private LocalDateTime dataConclusao;
    private Long adminId;
    private Long reportId;

    public Justificativa() {
    }


    public Justificativa(String fotoDepoisUrl, String notaTecnica, Long adminId, Long reportId) {
        this.fotoDepoisUrl = fotoDepoisUrl;
        this.notaTecnica = notaTecnica;
        this.adminId = adminId;
        this.reportId = reportId;
    }


    public Justificativa(Long id, String fotoDepoisUrl, String notaTecnica,
                         LocalDateTime dataConclusao, Long adminId, Long reportId) {
        this.id = id;
        this.fotoDepoisUrl = fotoDepoisUrl;
        this.notaTecnica = notaTecnica;
        this.dataConclusao = dataConclusao;
        this.adminId = adminId;
        this.reportId = reportId;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFotoDepoisUrl() { return fotoDepoisUrl; }
    public void setFotoDepoisUrl(String fotoDepoisUrl) { this.fotoDepoisUrl = fotoDepoisUrl; }

    public String getNotaTecnica() { return notaTecnica; }
    public void setNotaTecnica(String notaTecnica) { this.notaTecnica = notaTecnica; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
}