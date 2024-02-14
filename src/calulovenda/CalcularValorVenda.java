package calulovenda;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CalcularValorVenda {

    public Double calcularValorVenda() {
        boolean pagamentoAVista = true;
        LocalDate dataVenda = LocalDate.now();
        LocalDate dataPagamento = dataVenda.plusDays(40);
        Double valorVenda = 0d;
        int idEstado = 1;
        Double percentualDescontoAluno = calcularPercentualDescontoVeterano();
        List<Material> listaMateriais = retornarListaDeMaterial();

        //calcular valor total dos itens
        for (Material material : listaMateriais) {
            valorVenda = valorVenda + calcularValorVendaItem(material, percentualDescontoAluno);
        }

        //calcular desconto ou acrescimo pela data de pagamento
        if (pagamentoAVista) {
            valorVenda = valorVenda - calcularDescontoPagamentoAVista(valorVenda);
        } else {
            valorVenda = valorVenda + calcularAcresecimoPagamentoAPrazo(dataVenda, dataPagamento, valorVenda);
        }

        if (verificarSeAlunoPagaFrete(EnumModeloCompra.ONLINE, EnumTipoMaterial.IMPRESSO, true)) {
            valorVenda = valorVenda + retornarValorFrete(idEstado);
        }

        return valorVenda;
    }

    private Double calcularValorVendaItem(Material material, double percentDesconto) {
        return material.valorMaterial - (material.valorMaterial * percentDesconto);
    }

    private static double calcularPercentualDescontoVeterano() {
        ConsultasAoBancoDeDados consultasAoBancoDeDados = new ConsultasAoBancoDeDados();
        int qtdAnosAluno = consultasAoBancoDeDados.retornarQuantidadeDeAnosDoAlunoNaEscola();

        switch (qtdAnosAluno) {
            case 1:
                return 0.01;
            case 2:
                return 0.02;
            case 3:
                return 0.03;
            case 4:
                return 0.04;
            default:
                if (qtdAnosAluno >= 5) {
                    return 0.05;
                } else {
                    return 0;
                }
        }
    }

    private double calcularDescontoPagamentoAVista(Double valorVenda) {
        return (valorVenda * (0.05));
    }

    private double calcularAcresecimoPagamentoAPrazo(LocalDate dataVenda, LocalDate dataPagamento, Double valorVenda) {
        int qtdDiasUteis = calcularDiasUteis(dataVenda, dataPagamento);
        return (valorVenda * (0.0001)) * qtdDiasUteis;
    }

    private double retornarValorFrete(Integer idEstado) {
        ConsultasAoBancoDeDados consultasAoBancoDeDados = new ConsultasAoBancoDeDados();
        return consultasAoBancoDeDados.retornarValorFretePorEstado(idEstado);
    }

    private boolean verificarSeAlunoPagaFrete(EnumModeloCompra modeloCompra, EnumTipoMaterial tipoMaterial, Boolean necessitaEntrega) {
        return (modeloCompra.equals(EnumModeloCompra.ONLINE) && tipoMaterial.equals(EnumTipoMaterial.IMPRESSO))
                || (modeloCompra.equals(EnumModeloCompra.PRESENCIAL) && necessitaEntrega);
    }

    public int calcularDiasUteis(LocalDate dataInicial, LocalDate dataFinal) {
        int diasUteis = 0;
        LocalDate dataAtual = dataInicial;

        while (!dataAtual.isAfter(dataFinal)) {
            if (dataAtual.getDayOfWeek() != DayOfWeek.SATURDAY && dataAtual.getDayOfWeek() != DayOfWeek.SUNDAY) {
                diasUteis++;
            }
            dataAtual = dataAtual.plusDays(1);
        }

        return diasUteis;
    }
    
    private List<Material> retornarListaDeMaterial(){
        List<Material> listaMaterial = new ArrayList<>();
        
        Material material1 = new Material();
        material1.id = 1;
        material1.codigo = "cod123";
        material1.nome = "teste1";
        material1.valorMaterial = 20d;
        listaMaterial.add(material1);
        
        Material material2 = new Material();
        material2.id = 1;
        material2.codigo = "cod456";
        material2.nome = "teste2";
        material2.valorMaterial = 70d;
        listaMaterial.add(material2);
        
        Material material3 = new Material();
        material3.id = 1;
        material3.codigo = "cod789";
        material3.nome = "teste2";
        material3.valorMaterial = 10d;
        listaMaterial.add(material3);
        
        return listaMaterial;
    }
}
