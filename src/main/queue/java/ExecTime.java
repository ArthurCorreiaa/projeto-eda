package src.main.queue.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Classe para medição de tempos de execução de operações em uma estrutura Queue.
 * Gera arquivos de saída com os tempos medidos para análise de desempenho.
 */
public class ExecTime {
    // Nomes das operações que serão medidas
    private static final String[] METHOD_NAMES = {
        "remove_last",
        "add_last",
        "get_last",
        "remove_first",
        "add_first",
        "get_first",
        "remove_middle",
        "add_middle",
        "get_middle"
    };

    // Número de execuções para medição
    private static final int MEASUREMENT_RUNS = 30;
    // Diretório de saída
    private static final String OUTPUT_DIR = "data/results/time/";
    
    /**
     * Método principal que inicia a execução do benchmark.
     * @param args Argumentos de linha de comando (arquivo de entrada obrigatório)
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("ERRO: nenhum argumento!");
            System.exit(1);
        }

        // Arquivo de entrada com os dados de teste
        String inputFile = args[0];
        // Garante que o diretório de saída existe
        createOutputDirectory();

        try {
            // Lê todas as linhas do arquivo de entrada
            List<String> lines = readAllLines(inputFile);
            // Inicializa writers para os arquivos de saída
            PrintWriter[] writers = initializeWriters();

            // Processa cada linha e realiza as medições
            processLines(lines, writers);

            // Fecha todos os arquivos de saída
            closeWriters(writers);
            System.out.printf("\rFinalizado! Processadas %d linhas.%n", lines.size());
        } catch (IOException e) {
            System.err.println("ERRO: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Cria o diretório de saída se não existir.
     * Encerra o programa em caso de falha.
     */
    private static void createOutputDirectory() {
        File outputDirFile = new File(OUTPUT_DIR);
        if (!outputDirFile.exists() && !outputDirFile.mkdirs()) {
            System.err.println("Falha ao criar diretório de saída: " + OUTPUT_DIR);
            System.exit(1);
        }
    }

    /**
     * Lê todas as linhas de um arquivo, ignorando linhas em branco.
     * @param inputFile Caminho do arquivo de entrada
     * @return Lista de linhas lidas
     * @throws IOException Se ocorrer erro de leitura
     */
    private static List<String> readAllLines(String inputFile) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    /**
     * Inicializa os escritores para os arquivos de saída.
     * @return Array de PrintWriters, um para cada operação
     * @throws IOException Se ocorrer erro ao criar os arquivos
     */
    private static PrintWriter[] initializeWriters() throws IOException {
        PrintWriter[] writers = new PrintWriter[METHOD_NAMES.length];
        for (int i = 0; i < METHOD_NAMES.length; i++) {
            File outputFile = new File(OUTPUT_DIR + METHOD_NAMES[i] + ".data");
            boolean fileExists = outputFile.exists();

            // Abre arquivo em modo append (adiciona ao existente)
            writers[i] = new PrintWriter(new FileWriter(outputFile, true));

            // Se o arquivo não existia, escreve o cabeçalho
            if (!fileExists) {
                writers[i].println("estrutura_linguagem tempo tamanho");
            }
        }
        return writers;
    }

     /**
     * Processa cada linha do arquivo de entrada, realizando os testes.
     * @param lines Linhas a serem processadas
     * @param writers Escritores para os arquivos de saída
     */
    private static void processLines(List<String> lines, PrintWriter[] writers) {
        int totalLines = lines.size();
        for (int i = 0; i < totalLines; i++) {
            String line = lines.get(i);
            System.out.printf("\rProcessando linha %d/%d", i + 1, totalLines);
            System.out.flush();

            try {
                // Converte a linha em array de inteiros
                int[] elements = Arrays.stream(line.split("\\s+"))
                                     .mapToInt(Integer::parseInt)
                                     .toArray();
                // Realiza os testes com os elementos lidos
                testQueueOperations(writers, elements);
            } catch (NumberFormatException e) {
                System.err.printf("%nErro ao analisar linha %d: %s%n", i + 1, line);
            }
        }
    }

    /**
     * Fecha todos os escritores de arquivo.
     * @param writers Array de PrintWriters a serem fechados
     */
    private static void closeWriters(PrintWriter[] writers) {
        for (PrintWriter writer : writers) {
            if (writer != null) {
                writer.close();
            }
        }
    }
    
    /**
     * Testa todas as operações da fila com um conjunto de elementos.
     * @param writers Escritores para gravar resultados
     * @param elements Elementos a serem usados nos testes
     */
    private static void testQueueOperations(PrintWriter[] writers, int[] elements) {
        int length = elements.length;
        int middle = length / 2;
        long[][] allTimes = new long[METHOD_NAMES.length][MEASUREMENT_RUNS];

        // Inicializa a fila com os elementos fornecidos
        Queue<Integer> queue = new Queue<>(length);
        for (int num : elements) {
            queue.addLast(num);
        }
        // Fase de medição - registra os tempos
        for (int run = 0; run < MEASUREMENT_RUNS; run++) {
            performOperations(queue, middle, allTimes, run);
        }

        // Calcula a mediana dos tempos e escreve nos arquivos
        for (int method = 0; method < METHOD_NAMES.length; method++) {
            Arrays.sort(allTimes[method]);
            long medianTime = allTimes[method][MEASUREMENT_RUNS / 2];

            writers[method].printf("queue_java %d %d%n", medianTime, length);
            writers[method].flush();
        }
    }

    /**
     * Executa operações na fila.
     * @param queue Fila a ser testada
     * @param middle Posição média para operações
     * @param allTimes Array para armazenar tempos medidos
     * @param currentRun Número da execução atual
     */
    private static void performOperations(Queue<Integer> queue, int middle, long[][] allTimes, int currentRun) {
        allTimes[0][currentRun] = measureOperation(queue::removeLast);
        allTimes[1][currentRun] = measureOperation(() -> queue.addLast(999));
        allTimes[2][currentRun] = measureOperation(queue::getLast);
        allTimes[3][currentRun] = measureOperation(queue::removeFirst);
        allTimes[4][currentRun] = measureOperation(() -> queue.addFirst(999));
        allTimes[5][currentRun] = measureOperation(queue::getFirst);
        allTimes[6][currentRun] = measureOperation(() -> queue.remove(middle));
        allTimes[7][currentRun] = measureOperation(() -> queue.add(999, middle));
        allTimes[8][currentRun] = measureOperation(() -> queue.get(middle));
    }

    /**
     * Mede o tempo de execução de uma operação.
     * @param operation Operação a ser medida
     * @return Tempo de execução em nanossegundos
     */
    private static long measureOperation(Runnable operation) {
        long startTime = System.nanoTime();
        operation.run();
        long endTime = System.nanoTime();
        return endTime - startTime;
    }
}
