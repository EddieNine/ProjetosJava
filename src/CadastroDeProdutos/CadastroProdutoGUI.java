package CadastroDeProdutos;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CadastroProdutoGUI{
    private List<Produto> produtos = new ArrayList<>();
    private static final String ARQUIVO_PRODUTOS = "src/CadastroDeProdutos/produtos.txt";
    private JList<String> listaProdutos;

    public CadastroProdutoGUI() {
        JFrame frame = new JFrame("Cadastro de Produto");

        JLabel nomeLabel = new JLabel("Nome do Produto:");
        nomeLabel.setBounds(50, 50, 150, 20);

        JTextField nomeField = new JTextField();
        nomeField.setBounds(50, 70, 150, 20);

        JLabel precoLabel = new JLabel("Preço:");
        precoLabel.setBounds(50, 100, 150, 20);

        JTextField precoField = new JTextField();
        precoField.setBounds(50, 120, 150, 20);

        JLabel quantidadeLabel = new JLabel("Quantidade:");
        quantidadeLabel.setBounds(50, 150, 150, 20);

        JTextField quantidadeField = new JTextField();
        quantidadeField.setBounds(50, 170, 150, 20);

        JButton salvarButton = new JButton("Salvar");
        salvarButton.setBounds(50, 200, 150, 30);
        salvarButton.addActionListener(e -> salvarProduto(nomeField, precoField, quantidadeField));

        JButton editarButton = new JButton("Editar Produto");
        editarButton.setBounds(210, 200, 150, 30);
        editarButton.addActionListener(e -> editarProduto());

        JButton removerButton = new JButton("Remover Produto");
        removerButton.setBounds(50, 240, 150, 30);
        removerButton.addActionListener(e -> removerProduto());

        carregarProdutosDoArquivo();

        listaProdutos = new JList<>(getNomesProdutos());
        JScrollPane scrollPane = new JScrollPane(listaProdutos);
        scrollPane.setBounds(210, 50, 150, 140);

        frame.add(nomeLabel);
        frame.add(nomeField);
        frame.add(precoLabel);
        frame.add(precoField);
        frame.add(quantidadeLabel);
        frame.add(quantidadeField);
        frame.add(salvarButton);
        frame.add(editarButton);
        frame.add(removerButton);
        frame.add(scrollPane);

        frame.setSize(400, 400);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void salvarProduto(JTextField nomeField, JTextField precoField, JTextField quantidadeField) {
        String nome = nomeField.getText();
        String precoText = precoField.getText();
        String quantidadeText = quantidadeField.getText();

        if (nome.isEmpty() || precoText.isEmpty() || quantidadeText.isEmpty()) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos.");
            return;
        }

        try {
            double preco = Double.parseDouble(precoText);
            int quantidade = Integer.parseInt(quantidadeText);

            Produto produto = new Produto(nome, preco, quantidade);
            produtos.add(produto);
            salvarProdutoNoArquivo(produto);
            atualizarListaProdutos();
            JOptionPane.showMessageDialog(null, "Produto salvo:\n" + produto);

            nomeField.setText("");
            precoField.setText("");
            quantidadeField.setText("");
        } catch (NumberFormatException ex) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Formato inválido.");
        }
    }

    private void editarProduto() {
        int index = listaProdutos.getSelectedIndex();
        if (index != -1) {
            Produto produto = produtos.get(index);
            String novoNome = JOptionPane.showInputDialog("Novo nome:", produto.getNome());
            String novoPreco = JOptionPane.showInputDialog("Novo preço:", produto.getPreco());
            String novaQuantidade = JOptionPane.showInputDialog("Nova quantidade:", produto.getQuantidade());

            try {
                produto.setNome(novoNome);
                produto.setPreco(Double.parseDouble(novoPreco));
                produto.setQuantidade(Integer.parseInt(novaQuantidade));

                salvarTodosProdutosNoArquivo();
                atualizarListaProdutos();
                JOptionPane.showMessageDialog(null, "Produto editado com sucesso.");
            } catch (NumberFormatException ex) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null, "Formato inválido.");
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Selecione um produto para editar.");
        }
    }

    private void removerProduto() {
        int index = listaProdutos.getSelectedIndex();
        if (index != -1) {
            produtos.remove(index);
            salvarTodosProdutosNoArquivo();
            atualizarListaProdutos();
            JOptionPane.showMessageDialog(null, "Produto removido com sucesso.");
        } else {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Selecione um produto para remover.");
        }
    }

    private void salvarProdutoNoArquivo(Produto produto) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_PRODUTOS, true))) {
            writer.write(produto.toString());
            writer.newLine();
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Erro ao salvar o produto no arquivo.");
        }
    }

    private void salvarTodosProdutosNoArquivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_PRODUTOS))) {
            for (Produto produto : produtos) {
                writer.write(produto.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Erro ao salvar os produtos no arquivo.");
        }
    }

    private void carregarProdutosDoArquivo() {
        try {
            File arquivo = new File(ARQUIVO_PRODUTOS);

            // Se o arquivo não existir, crie-o
            if (!arquivo.exists()) {
                arquivo.createNewFile();
            }

            BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_PRODUTOS));
            String linha;
            while ((linha = reader.readLine()) != null) {
                Produto produto = Produto.fromString(linha);  // Converte a linha para um objeto Produto
                produtos.add(produto);  // Adiciona o produto à lista
            }
            reader.close();
        } catch (IOException e) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Erro ao carregar os produtos do arquivo.");
            e.printStackTrace();  // Imprime o erro no console para depuração
        }
    }


    private void atualizarListaProdutos() {
        listaProdutos.setListData(getNomesProdutos());
    }

    private String[] getNomesProdutos() {
        return produtos.stream().map(Produto::getNome).toArray(String[]::new);
    }

    public static void main(String[] args) {
        new CadastroProdutoGUI();
    }
}
class Produto {
    private String nome;
    private double preco;
    private int quantidade;

    public Produto(String nome, double preco, int quantidade) {
        this.nome = nome;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return String.format("Nome: %s | Preço: R$ %.2f | Quantidade: %d", nome, preco, quantidade);
    }

    public static Produto fromString(String linha) {
        String[] partes = linha.split("\\|");
        String nome = partes[0].split(":")[1].trim();
        // Substituir a vírgula por ponto para a conversão correta
        String precoString = partes[1].split("R\\$")[1].trim().replace(',', '.');
        double preco = Double.parseDouble(precoString);
        int quantidade = Integer.parseInt(partes[2].split(":")[1].trim());
        return new Produto(nome, preco, quantidade);
    }
}
