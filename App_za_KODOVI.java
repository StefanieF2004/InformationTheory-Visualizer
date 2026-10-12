import javax.swing.*;//za graficki kontroli (JFrame,JPanel..)
import javax.swing.table.DefaultTableModel;
import java.awt.*;//za grafikata i dizajnot(Font, Color..)
import java.awt.event.*;//za inteerakcija so korisnikot
//za strukturite od podatoci
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
  GLAVNA KLASA: App_za_KODOVI
  Ova e glavniot prolozec koj ovozmozuva vnesuvanje na verojatnosti,
  izvrsuvanje i cekor-po-cekor vizuelizacija na cetiri poznati algoritmi od Teorija na Informacii:
  1. Shannon-Fano, 2. Huffman, 3. Binaren kod na Shannon, 4. Gilbert-Moore.
 */
public class App_za_KODOVI extends JFrame {

    /**
      POMOSNA KLASA: Symbol
      Gi pretstavuva vleznite simboli od izvorot na informacii (na pr. alfa_1, alfa_2...).
      Za sekoj simbol se cuva negovoto ime, verojatnosta i krajniot kod dobien od algoritmot.
     */
    static class Symbol {
        String name;          // Ime na simbolot (primer: "a1")
        double probability;   // Verojatnost na pojavuvanje (p_i)
        String code = "";     // Krajniot binaren kod (primer: "010")

        public Symbol(String name, double probability) {
            this.name = name;
            this.probability = probability;
        }
    }

    /**
      POMOSNA KLASA: Node
      Pretstavuva jazol vo drvoto na kodiranje (se koristi za Huffman i Shannon-Fano).
      Ovaa klasa gi cuva koordinatite (x, y) za crtanje.
     */
    static class Node {
        double prob;                // Vkupna verojatnost na ovoj jazol (ili suma na poddrvoto)
        String text = "";           // Tekstot koj kje se ispise vo meurcheto/kockata
        List<Node> children = new ArrayList<>(); // Deca-jazli (granki vo drvoto)
        int x, y;                   // Koordinati na ekranot kade kje se iscrta jazolot
        boolean isLeaf;             // Zname dali jazolot e list (krajna bukva) ili vnatresen jazol

        public Node(String text, double prob, boolean isLeaf) {
            this.text = text;
            this.prob = prob;
            this.isLeaf = isLeaf;
        }
        
        /**
          Se koristi za da se zacuva momentalnata sostojba na drvoto vo odreden cekor,
          bez potnatamosnite promeni da go rasipat prikazot za toj cekor.
         */
        public Node makeCopy() {
            Node copy = new Node(this.text, this.prob, this.isLeaf);
            copy.x = this.x;
            copy.y = this.y;
            for (Node child : this.children) {
                copy.children.add(child.makeCopy()); // Rekurzivno kopiranje na decata
            }
            return copy;
        }
    }

    /**
      POMOSNA KLASA: VisualLine
      Sluzi isklucivo za grafickiot panel. Gi zacuvuva pocetnite i krajnite koordinati
      na liniite koi gi povrzuvaat jazlite vo drvoto, kako i tekstot (0 ili 1) nad samata linija.
     */
    static class VisualLine {
        int x1, y1, x2, y2; // Koordinati na liniite
        String label;       // Oznaka na grankata (najcesto "0" za levo, "1" za desno)

        public VisualLine(int x1, int y1, int x2, int y2, String label) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.label = label;
        }
    }

    // --- GUI KOMPONENTI (Elementi od korisnickiot vmesnik) ---
    private JTextField txtProbabilities; // Pole za vnes na verojatnostite
    private JTextField txtBaseB;         // Pole za osnovata 'b' 
    private JComboBox<String> comboAlgorithm; // Padagjacko meni za izbor na algoritam
    private JButton btnInitialize, btnNext, btnPrev; // Kopcinja za kontrola i navigacija
    private JTable resultTable;          // Tabela za prikaz na krajnite rezultati (simbol, verojatnost, kod, dolzina)
    private DefaultTableModel tableModel; // Model za upravuvanje so podatocite vo tabelata
    private JLabel lblStats;             // Statusna linija na dnoto (za Entropija, Prosecna dolzina, Efikasnost)
    private JTextArea txtDetailedMath;   // Tekstualno del kade se ispisuva matematickata postapka cekor-po-cekor
    private GraphicsPanel drawingPanel;  // Custom panel kade sto vmetnuvame grafika (se crta drvoto/blokovite)

    // --- GLOBALNI PROMENLIVI ZA SLEDENJE NA SOSTOJBATA (State Management) ---
    private List<Node> globalShannonRootHolder = new ArrayList<>(); // Drzi go korenot za Shannon-Fano
    private List<List<Node>> stepNodesToDraw = new ArrayList<>();   // Lista od jazli za sekoj cekor posebno
    private List<List<VisualLine>> stepLinesToDraw = new ArrayList<>(); // Lista od linii za sekoj cekor posebno
    private List<String> stepMathTexts = new ArrayList<>();         // Matematicko objasnuvanje za sekoj cekor posebno
    private int currentStepIndex = -1;   // Indeks na momentalno prikazaniot cekor (-1 znaci deka seuste ne e pocnato)
    private List<Symbol> processedSymbols = new ArrayList<>();     // Lista na obraboteni simboli

    /**
      KONSTRUKTOR NA APLIKACIJATA
      Tuka se definiraat goleminata na prozorcot, se kreiraat panelite, se rasporeduvaat
      elementite (BorderLayout, FlowLayout, GridLayout) i se prikacuvaat kopcinjata.
     */
    public App_za_KODOVI() {
        setTitle("Теорија на Информации - Компактна Визуелизација со Меурчиња");
        setSize(1450, 900); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centriraj go prozorcot na ekranot
        setLayout(new BorderLayout(12, 12)); // Glaven raspored so rastoianie od 12 pikseli

        // 1. GOREN DEL - Za vnes na podatoci i izbor na algoritam
        JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelInput.setBorder(BorderFactory.createTitledBorder(" Влезни Параметри и Избор на Алгоритам "));

        panelInput.add(new JLabel("Веројатности (p_i):"));
        txtProbabilities = new JTextField("0.49 0.26 0.12 0.04 0.04 0.03 0.02", 35);
        panelInput.add(txtProbabilities);

        panelInput.add(new JLabel("Основа b:"));
        txtBaseB = new JTextField("2", 3);
        panelInput.add(txtBaseB);

        panelInput.add(new JLabel("Код:"));
        comboAlgorithm = new JComboBox<>(new String[]{
            "Shannon-Fano Код",
            "Huffman Алгоритам", 
            "Бинарен код на Shannon", 
            "Код на Gilbert-Moore"
        });
        panelInput.add(comboAlgorithm);

        btnInitialize = new JButton("ВЧИТАЈ");
        btnInitialize.setBackground(new Color(41, 128, 185)); // Sina boja za kopceto vcitaj
        btnInitialize.setForeground(Color.WHITE);
        panelInput.add(btnInitialize);

        btnPrev = new JButton("◀ ПРЕТХОДЕН ЧЕКОР");
        btnPrev.setEnabled(false); // Na pocetok e iskluceno
        panelInput.add(btnPrev);

        btnNext = new JButton("СЛЕДЕН ЧЕКОР ▶");
        btnNext.setEnabled(false); // Na pocetok e iskluceno
        panelInput.add(btnNext);

        add(panelInput, BorderLayout.NORTH);

        // 2. CENTRALEN DEL - Podelen na dva dela (Levo: Tabela i matematicki presmetki, Desno: Grafika)
        JPanel panelCenter = new JPanel(new GridLayout(1, 2, 12, 12));

        // Lev del od centarot: Tabela gore i matematicki presmetki
        JPanel panelLeftContainer = new JPanel(new BorderLayout(5, 5));
        String[] columns = {"Симбол (α_i)", "Веројатност (p_i)", "Кодна замена f(α_i)", "Должина (n_i)"};
        tableModel = new DefaultTableModel(columns, 0);
        resultTable = new JTable(tableModel);
        resultTable.setFont(new Font("Arial", Font.PLAIN, 14));
        resultTable.setRowHeight(26);
        JScrollPane scrollTable = new JScrollPane(resultTable);
        scrollTable.setPreferredSize(new Dimension(600, 240));
        panelLeftContainer.add(scrollTable, BorderLayout.NORTH);

        txtDetailedMath = new JTextArea();
        txtDetailedMath.setFont(new Font("Monospaced", Font.PLAIN, 13)); // Monospaced font za poubav prikaz na brojki
        txtDetailedMath.setEditable(false);
        txtDetailedMath.setBackground(new Color(248, 249, 250));
        JScrollPane scrollMath = new JScrollPane(txtDetailedMath);
        scrollMath.setBorder(BorderFactory.createTitledBorder(" Чекор-по-чекор Математичка Постапка "));
        panelLeftContainer.add(scrollMath, BorderLayout.CENTER);

        panelCenter.add(panelLeftContainer);
        
        // Desniot del od centarot go zafakja panelot za crtanje grafovi toest drva
        panelCenter.add(drawingPanel = new GraphicsPanel());
        add(panelCenter, BorderLayout.CENTER);

        // 3. DOLEN DEL - Prikaz na finalnata statistika
        JPanel panelBottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lblStats = new JLabel("Внесете веројатности и притиснете 'ВЧИТАЈ' за почеток.", SwingConstants.CENTER);
        lblStats.setFont(new Font("Arial", Font.BOLD, 14));
        panelBottom.add(lblStats);
        add(panelBottom, BorderLayout.SOUTH);

        // Povrzuvanje na kopcinjata so soodvetnite metodi preku Lambda izrazi
        btnInitialize.addActionListener(e -> initializeAlgorithm());
        btnNext.addActionListener(e -> navigateStep(1));
        btnPrev.addActionListener(e -> navigateStep(-1));
    }

    /**
      Se povikuva koga korisnikot kje klikne "VČITAЈ".
      Ja vrsi klucnata validacija na vlezot: dali se broevi, dali se megu 0 i 1,
      i najvazno - dali sumata na site verojatnosti e tocno 1.0.
      Ako se e vo red, go aktivira izbraniot algoritam.
     */
    private void initializeAlgorithm() {
        try {
            int b = Integer.parseInt(txtBaseB.getText().trim());
            String rawInput = txtProbabilities.getText().trim().replaceAll(",", " ");
            String[] tokens = rawInput.split("\\s+"); // Podeli go tekstot po prazni mesta
            
            List<Symbol> temporarySymbols = new ArrayList<>();
            double sumOfProbabilities = 0.0;

            for (int i = 0; i < tokens.length; i++) {
                if (!tokens[i].isEmpty()) {
                    double val = Double.parseDouble(tokens[i].trim());
                    
                    // Validacija: sekoja poedinecna verojatnost mora da e vo [0,1]
                    if (val < 0 || val > 1) {
                        JOptionPane.showMessageDialog(this, 
                            "Грешка! Веројатноста мора да биде помеѓу 0 и 1 (Грешка кај вредноста: " + val + ").", 
                            "Невалиден внес", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    temporarySymbols.add(new Symbol("α" + (temporarySymbols.size() + 1), val));
                    sumOfProbabilities += val;
                }
            }

            // Validacija: Sumata mora da bide 1.0 (so mala tolerancija za decimalna greska)
            if (Math.abs(sumOfProbabilities - 1.0) > 0.0001) {
                JOptionPane.showMessageDialog(this, 
                    String.format("Грешка! Сумата на веројатностите МОРА да биде 1.0.\nТековна сума: %.4f", sumOfProbabilities), 
                    "Математичка грешка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ako validacijata e uspesna, gi resetirame listite so cekori i pocnuvame odnovo
            processedSymbols.clear();
            processedSymbols.addAll(temporarySymbols);
            
            stepNodesToDraw.clear();
            stepLinesToDraw.clear();
            stepMathTexts.clear();
            globalShannonRootHolder.clear();
            txtDetailedMath.setText(""); 
            currentStepIndex = 0;

            String selectedAlgo = (String) comboAlgorithm.getSelectedItem();

            // Vo zavisnost od algoritmot, vrsimo sortiranje (opagacko/rastecko) i go povikuvame metodot
            if (selectedAlgo.contains("Huffman")) {
                // Huffman bara rastecko sortiranje (od najmali kon najgolemi verojatnosti)
                processedSymbols.sort((s1, s2) -> Double.compare(s1.probability, s2.probability));
                generateHuffmanTree(b);
            } else if (selectedAlgo.contains("Shannon-Fano")) {
                // Shannon-Fano bara opagacko sortiranje
                processedSymbols.sort((s1, s2) -> Double.compare(s2.probability, s1.probability));
                generateShannonFanoTree();
            } else if (selectedAlgo.contains("Бинарен код на Shannon")) {
                generateShannonBinary();
            } else if (selectedAlgo.contains("Gilbert-Moore")) {
                generateGilbertMoore();
            }

            displayStep(); // Prikazi go prviot cekor
            btnNext.setEnabled(stepNodesToDraw.size() > 1); // Ako ima poveke cekori, aktiviraj go "Sleden"
            btnPrev.setEnabled(false);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Грешка! Ве молиме проверете го форматот на внесените броеви.", "Грешка при внес", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
      ALGORITAM 1: Shannon-Fano - Inicijalizacija
      Go kreira pocetniot koren na drvoto koj gi sodrzi site simboli i ja zapocnuva rekurzijata.
     */
    private void generateShannonFanoTree() {
        StringBuilder sbRoot = new StringBuilder();
        double totalP = 0;
        List<Symbol> initialList = new ArrayList<>();
        
        for (Symbol s : processedSymbols) {
            sbRoot.append(s.name).append(" ");
            totalP += s.probability;
            initialList.add(s);
        }
        
        Node root = new Node(sbRoot.toString().trim() + " = " + String.format("%.2f", totalP), totalP, false);
        root.x = 350; root.y = 50; // Pocetna pozicija na korenot vo panelot
        globalShannonRootHolder.add(root);

        List<Node> initialNodes = new ArrayList<>();
        List<VisualLine> initialLines = new ArrayList<>();
        collectElements(root, initialNodes, initialLines);
        
        stepNodesToDraw.add(initialNodes);
        stepLinesToDraw.add(initialLines);
        stepMathTexts.add(" ШЕНОН-ФАНО КОД\nПочетна состојба: Коренот ги содржи сите елементи.");

        // Povikaj go rekurzivniot metod za delenje na grupite
        divideShannonFanoRecursive(root, initialList, 180, "");
    }

    /**
      Klucot na Shannon-Fano: Listata se deli na dva dela taka sto razlikata megu
      sumata na verojatnostite na levata i desnata grupa e MINIMALNA.
      Na levata grupa i se dodava bit "0", na desnata "1".
     */
    private void divideShannonFanoRecursive(Node parent, List<Symbol> symbols, int xOffset, String currentCode) {
        if (symbols == null || symbols.isEmpty()) return;
        //  Ako ostana samo eden simbol, toj e list i mu go dodeluvame kodot
        if (symbols.size() == 1) {
            Symbol s = symbols.get(0);
            parent.text = s.name + " = " + s.probability;
            parent.isLeaf = true;
            s.code = currentCode;
            return;
        }
        double totalSum = 0;
        for (Symbol s : symbols) totalSum += s.probability;

        int splitIndex = 0;
        double minDiff = Double.MAX_VALUE;
        double leftGroupSum = 0;
        double runningSum = 0;
        // Naoganje na optimalnata tocka na delenje 
        for (int i = 0; i < symbols.size() - 1; i++) {
            runningSum += symbols.get(i).probability;
            double rightSum = totalSum - runningSum;
            double currentDiff = Math.abs(runningSum - rightSum);

            if (currentDiff < minDiff) {
                minDiff = currentDiff;
                splitIndex = i;
                leftGroupSum = runningSum;
            }
        }
        double rightGroupSum = totalSum - leftGroupSum;
        // Podeli ja listata na leva i desna podlista
        List<Symbol> leftSymbols = new ArrayList<>(symbols.subList(0, splitIndex + 1));
        List<Symbol> rightSymbols = new ArrayList<>(symbols.subList(splitIndex + 1, symbols.size()));
        // Kreiranje na levoto dete vo drvoto
        StringBuilder leftText = new StringBuilder();
        for (Symbol s : leftSymbols) leftText.append(s.name).append(" ");
        Node leftChild = new Node(leftText.toString().trim(), leftGroupSum, false);
        leftChild.x = parent.x - xOffset;
        leftChild.y = parent.y + 55; 
        parent.children.add(leftChild);
        // Kreiranje na desnoto dete vo drvoto
        StringBuilder rightText = new StringBuilder();
        for (Symbol s : rightSymbols) rightText.append(s.name).append(" ");
        Node rightChild = new Node(rightText.toString().trim(), rightGroupSum, false);
        rightChild.x = parent.x + xOffset;
        rightChild.y = parent.y + 55; 
        parent.children.add(rightChild);
        // Zapisi go ovoj cekor vo tekstot za matematika
        StringBuilder sbMath = new StringBuilder("ШЕНОН-ФАНО ОПТИМАЛНО ДЕЛЕЊЕ\n");
        sbMath.append(String.format(" -> Гранка 0 (Лево): [%s] со сума = %.4f\n", leftChild.text, leftGroupSum));
        sbMath.append(String.format(" -> Гранка 1 (Десно): [%s] со сума = %.4f\n", rightChild.text, rightGroupSum));

        List<Node> currentTreeNodes = new ArrayList<>();
        List<VisualLine> currentTreeLines = new ArrayList<>();
        // Go kopirame celoto drvo za da go vizuelizirame momentalniot progres
        collectElements(globalShannonRootHolder.get(0).makeCopy(), currentTreeNodes, currentTreeLines);
        
        stepNodesToDraw.add(currentTreeNodes);
        stepLinesToDraw.add(currentTreeLines);
        stepMathTexts.add(sbMath.toString());

        // Prodolzi rekurzivno nadolu po drvoto
        divideShannonFanoRecursive(leftChild, leftSymbols, Math.max(xOffset / 2, 25), currentCode + "0");
        divideShannonFanoRecursive(rightChild, rightSymbols, Math.max(xOffset / 2, 25), currentCode + "1");
    }

    /**
      ALGORITAM 2: Huffman-ov Algoritam
      Raboti "од долу нагоре" (Bottom-Up). Gi zima dva (ili b) jazli so najmala verojatnost,
      gi spojuva vo eden roditel-jazol so suma od nivnite verojatnosti, i postapkata prodolzuva
      se dodeka ne ostane samo eden edinstven koren.
     */
    private void generateHuffmanTree(int b) {
        List<Node> forest = new ArrayList<>(); // "Suma" od nezavisni drva na pocetokot
        StringBuilder sbInit = new StringBuilder(" ХOФМАНОВ КОД \nИницијализација на јазли.\n");
        
        for (Symbol s : processedSymbols) {
            forest.add(new Node(s.name + "=" + s.probability, s.probability, true));
        }
        // AKO KODOT NE E BINAREN (b > 2): Mora da dodademe  jazli so verojatnost 0.0
        // za da bide ispolnet uslovot (N - 1) % (b - 1) == 0, inaku poslednoto spojuvanje nema na ima 'b' elementi.
        int dummyCount = (forest.size() - 1) % (b - 1);
        if (dummyCount != 0) {
            dummyCount = (b - 1) - dummyCount;
            for (int i = 0; i < dummyCount; i++) {
                forest.add(0, new Node("dummy=0.0", 0.0, true));
            }
        }
        updateForestLayout(forest, 500, -45); // Postavi gi pocetno jazlite na dnoto na ekranot
        saveState(forest, sbInit.toString());
        // Glavna cilka na Huffman: Spojuvaj se dodeka ne ostane samo eden koren
        while (forest.size() > 1) {
            forest.sort((n1, n2) -> Double.compare(n1.prob, n2.prob)); // Sortiraj po verojatnost rastecki
            int combineCount = Math.min(b, forest.size()); // Kolku elementi spojuvame vo ovoj cekor
            
            Node parent = new Node("Σ", 0.0, false); // Nov roditel-jazol (Suma)
            for (int i = 0; i < combineCount; i++) {
                Node child = forest.remove(0); // Izvleci go najmaliot
                parent.prob += child.prob;
                parent.children.add(child); // Dodaj go kako dete
            }
            forest.add(parent); // Vrati go roditelot nazad vo sumata (forest)

            updateForestLayout(forest, 500, -45); 
            saveState(forest, "Спојување на јазли со најмала веројатност.");
        }
        // Koga kje zavrsi spojuvanjeto, imame kompletno izgradeno drvo.
        Node finalRoot = forest.get(0);
        assignHuffmanCodes(finalRoot, ""); // Rekurzivno dodeli gi kodovite (0, 1...) od gore nadole
        calculateSubtreePositions(finalRoot, 350, 50, 160, 55); // Presmetaj poubavi pozicii za finalnoto drvo
        saveState(forest, "Изградено конечно Хофманово дрво.");
    }

    /**
      Pominuva niz izgradenoto drvo od gore nadolu. Na grankite im se dodeluvaat
      vrednosti od 0 do (b-1) vo zavisnost od redosledot na spojuvanje.
     */
    private void assignHuffmanCodes(Node node, String code) {
        if (node.isLeaf) {
            for (Symbol s : processedSymbols) {
                if (node.text.startsWith(s.name + "=")) {
                    s.code = code; // Dodeli go kodot na simbolot
                    break;
                }
            }
        }
        int count = node.children.size();
        for (int i = 0; i < count; i++) {
            // Rekurziven povik za sekoe dete so dodavanje na indeksot na grankata na krajot od kodot
            assignHuffmanCodes(node.children.get(i), code + (count - 1 - i));
        }
    }


    // Gi rasporeduva nezavisnite drva linearno na ekranot duri trae postapkata na spojuvanje kaj Huffman.     
    private void updateForestLayout(List<Node> forest, int startY, int levelGap) {
        int totalWidth = forest.size() * 110;
        int startX = 350 - (totalWidth / 2) + 55;
        for (int i = 0; i < forest.size(); i++) {
            calculateSubtreePositions(forest.get(i), startX + (i * 110), startY, 70, levelGap);
        }
    }

    /**
      Rekurziven algoritam koj gi presmetuva matematički točnite X i Y koordinati za sekoj
      jazol na ekranot, so cel drvoto da izgleda simetrično i pregledno bez prekloпување.
     */
    private void calculateSubtreePositions(Node node, int x, int y, int xOffset, int yOffset) {
        node.x = x;
        node.y = y;
        int count = node.children.size();
        for (int i = 0; i < count; i++) {
            int cx = x + (int) ((i - (count - 1) / 2.0) * xOffset);
            int cy = y + yOffset;
            calculateSubtreePositions(node.children.get(i), cx, cy, Math.max(xOffset / 2, 25), yOffset);
        }
    }

    /**
      Go zacuvuva momentalniot izgled na site jazli i linii vo tekovniot cekor
      i go zacuvuva vo listite za vizuelizacija. Ovozmozuva kopcinjata "Sleden" i "Prethoden" da rabotat.
     */
    private void saveState(List<Node> forest, String mathText) {
        List<Node> nodesToDraw = new ArrayList<>();
        List<VisualLine> linesToDraw = new ArrayList<>();
        for (Node root : forest) {
            Node rootCopy = root.makeCopy(); // Zadolzitelno kopiranje
            collectElements(rootCopy, nodesToDraw, linesToDraw);
        }
        stepNodesToDraw.add(nodesToDraw);
        stepLinesToDraw.add(linesToDraw);
        stepMathTexts.add(mathText);
    }

    /**
      Pominuva niz celo drvo i gi sobira site jazli i linii vo ednostavni ravni listi 
      koi grafickiot panel kje moze lesno da gi iscrta.
     */
    private void collectElements(Node node, List<Node> nodes, List<VisualLine> lines) {
        nodes.add(node);
        int count = node.children.size();
        for (int i = 0; i < count; i++) {
            Node child = node.children.get(i);
            lines.add(new VisualLine(node.x, node.y, child.x, child.y, String.valueOf(count - 1 - i)));
            collectElements(child, nodes, lines);
        }
    }

    /**
      ALGORITAM 3: Binaren kod na Shannon
      Ovoj algoritam ne koristi drvo, tuku presmetuva kumulativni verojatnosti q_i,
      ja naogja dolzinata na kodot, i potoa ja pretvora 
      decimalnata vrednost q_i vo binaren broj so zadadenata dolzina n_i.
     */
    private void generateShannonBinary() {
        double[] q = new double[processedSymbols.size()];
        q[0] = 0.0; // Prvata kumulativna verojatnost e sekogas nula
        // Presmetka na kumulativni verojatnosti: q_i = q_(i-1) + p_(i-1)
        for (int i = 0; i < processedSymbols.size() - 1; i++) {
            q[i + 1] = q[i] + processedSymbols.get(i).probability;
        }
        List<Node> currentBlocks = new ArrayList<>();
        int ty = 50;
        for (int i = 0; i < processedSymbols.size(); i++) {
            Symbol s = processedSymbols.get(i);
            // Formula za dolzina na kodot: ni = ceil(-log2(p_i))
            int ni = (int) Math.ceil(-Math.log(s.probability) / Math.log(2));
            StringBuilder sbMath = new StringBuilder();
            sbMath.append("БИНАРЕН ШЕНОН КОД (Чекор за ").append(s.name).append(")\n\n");
            if (i == 0) {
                sbMath.append("1. Кумулативна веројатност q1 = 0.0000 (Почетен елемент)\n");
            } else {
                sbMath.append("1. Кумулативна веројатност:\n");
                sbMath.append("   q_").append(i + 1).append(" = q_").append(i).append(" + p_").append(i).append("\n");
                sbMath.append(String.format("   q_%d = %.4f + %.4f = %.4f\n", i + 1, q[i - 1], processedSymbols.get(i - 1).probability, q[i]));
            }
            sbMath.append("\n2. Одредување на кодна должина (n_i):\n");
            sbMath.append("   Услов: p_i >= 1 / (2^n_i)  =>  2^(-n_i) <= p_i\n");
            double checkVal = Math.pow(2, -ni);
            sbMath.append(String.format("   За n_%d = %d:\n", i + 1, ni));
            sbMath.append(String.format("   --> %.4f >= 1 / (2^%d) = %.5f (Условот е исполнет!)\n", s.probability, ni, checkVal));
            sbMath.append("\n3. Пресметка на бинарен код преку множење на q_").append(i + 1).append(" со 2:\n");
            List<String> steps = new ArrayList<>();
            // Konverzija vo binaren zapis so detalno matemeticko objasnenie
            s.code = convertToBinaryWithExplanation(q[i], ni, steps);
            for (String step : steps) {
                sbMath.append(step).append("\n");
            }
            sbMath.append("   Добиен код за ").append(s.name).append(" = ").append(s.code).append("\n");

            // Kreirame HTML block/jazol za vizuelen prikaz na ekranot
            Node block = new Node("<html><center><b>" + s.name + "</b><br>q=" + String.format("%.4f", q[i]) + "<br>Код: <b>" + s.code + "</b></center></html>", s.probability, true);
            block.x = 350; block.y = ty;
            currentBlocks.add(block);
            ty += 65; // Pomesti go sledniot blok podole

            stepNodesToDraw.add(new ArrayList<>(currentBlocks));
            stepLinesToDraw.add(new ArrayList<>()); // Nema linii bidejki nema drvo
            stepMathTexts.add(sbMath.toString());
        }
    }

    /**
     ALGORITAM 4: Kod na Gilbert-Moore
     Slicen na Shannon, no ova e "Aritmeticki" kod koj ja zapazuva abeced-ната niza na simbolite.
     Tuka modifikacijata e sto q_{GM} se dodava polovina od sopstvenata verojatnost.
     */
    private void generateGilbertMoore() {
        double cumulativeSum = 0.0;
        List<Node> currentBlocks = new ArrayList<>();
        int ty = 50;

        for (int i = 0; i < processedSymbols.size(); i++) {
            Symbol s = processedSymbols.get(i);
            double qi = cumulativeSum + (s.probability / 2.0); // Klucna formula za Gilbert-Moore
            int ni = (int) Math.ceil(-Math.log(s.probability) / Math.log(2)) + 1; // Dolzina + 1 bit plus
            
            StringBuilder sbMath = new StringBuilder();
            sbMath.append("=== КОД НА ГИЛБЕРТ-МУР (Чекор за ").append(s.name).append(") ===\n\n");
            
            sbMath.append("1. Пресметка на вредноста q_").append(i + 1).append(":\n");
            sbMath.append("   Формула: q_i = Сума_(j<i)(p_j) + (p_i / 2)\n");
            if (i == 0) {
                sbMath.append(String.format("   q_1 = 0.0 + (p_1 / 2) = 0.0 + (%.4f / 2) = %.4f\n", s.probability, qi));
            } else {
                sbMath.append(String.format("   q_%d = %.4f + (%.4f / 2)\n", i + 1, cumulativeSum, s.probability));
                sbMath.append(String.format("   q_%d = %.4f + %.4f = %.4f\n", i + 1, cumulativeSum, s.probability / 2.0, qi));
            }

            sbMath.append("\n2. Одредување на кодна должина n_i:\n");
            sbMath.append("   Неравенство: 2^(-n_i) <= p_i < 2^(-n_i + 1)\n");
            double lowBound = Math.pow(2, -ni);
            double highBound = Math.pow(2, -ni + 1);
            sbMath.append(String.format("   За назначена должина n_%d = %d:\n", i + 1, ni));
            sbMath.append(String.format("   --> 2^(-%d) <= %.4f < 2^(-%d + 1)\n", ni, s.probability, ni));
            sbMath.append(String.format("   --> %.5f <= %.4f < %.5f  (НЕРАВЕНСТВОТО Е ТОЧНО!)\n", lowBound, s.probability, highBound));

            sbMath.append("\n3. Конверзија преку бинарно делење (множење со 2) на вредноста q_").append(i + 1).append(" (").append(String.format("%.4f", qi)).append("):\n");
            List<String> steps = new ArrayList<>();
            s.code = convertToBinaryWithExplanation(qi, ni, steps);
            for (String step : steps) {
                sbMath.append(step).append("\n");
            }
            sbMath.append("   Конечен коден збор за ").append(s.name).append(" = ").append(s.code).append("\n");

            Node block = new Node("<html><center><b>" + s.name + "</b><br>q_GM=" + String.format("%.4f", qi) + "<br>Код: <b>" + s.code + "</b></center></html>", s.probability, true);
            block.x = 350; block.y = ty;
            currentBlocks.add(block);
            ty += 65;

            cumulativeSum += s.probability;

            stepNodesToDraw.add(new ArrayList<>(currentBlocks));
            stepLinesToDraw.add(new ArrayList<>());
            stepMathTexts.add(sbMath.toString());
        }
    }

    /**
     Matematicka postapka za pretvoranje na decimalen broj pomal od 1 vo binarna forma.
     Brojot rekurzivno se mnozi so 2: celiot del (0 ili 1) stanuva bit, a decimalniot del prodolzuva
     da se mnozi se dodeka ne ja dobieme baranata dolzina 'len'.
     */
    private String convertToBinaryWithExplanation(double val, int len, List<String> stepsContainer) {
        StringBuilder sb = new StringBuilder();
        double t = val;
        for (int i = 1; i <= len; i++) {
            double oldT = t;
            t *= 2;
            int digit = (int) t; // Zimi go celiot del (0 ili 1)
            stepsContainer.add(String.format("   Чекор %d: %.4f * 2 = %.4f -> цифра/цел дел: %d", i, oldT, t, digit));
            sb.append(digit);
            t -= digit; // Odstrani go celiot del za slednoto mnozenje
        }
        return sb.toString();
    }

    /**
      Upravuva so klikanjeto na "Sleden" (+1) i "Prethoden" (-1) cekor.
      Gi menuva indeksot i gi azurira kopcinjata (gi gasni ako sme stignale na kraj ili na pocetok).
     */
    private void navigateStep(int direction) {
        currentStepIndex += direction;
        displayStep();
        btnPrev.setEnabled(currentStepIndex > 0);
        btnNext.setEnabled(currentStepIndex < stepNodesToDraw.size() - 1);
    }

    
      //Ja isrtuva i postavuva sodrzinata (grafika i tekst) za tekovniot indeks na cekorot.
    private void displayStep() {
        if (currentStepIndex >= 0 && currentStepIndex < stepNodesToDraw.size()) {
            drawingPanel.setGraphContent(stepNodesToDraw.get(currentStepIndex), stepLinesToDraw.get(currentStepIndex));
            if (currentStepIndex < stepMathTexts.size()) {
                txtDetailedMath.setText(stepMathTexts.get(currentStepIndex));
            }
            // Tabelata se popolnuva kompletno samo ako sme stignale na posledniot cekor
            updateTableData(currentStepIndex == stepNodesToDraw.size() - 1);
        }
    }

    /**
      METOD: updateTableData
      Presmetuva tri klucni entiteti vo Teorijata na Informacii na samiot kraj:
      1. Entropija
      2. Prosecna dolzina na kodot 
      3. Efikasnost 
     */
    private void updateTableData(boolean fullyDone) {
        tableModel.setRowCount(0); // Ischisti ja tabelata
        double entropy = 0.0;
        double avgLength = 0.0;

        for (int i = 0; i < processedSymbols.size(); i++) {
            Symbol s = processedSymbols.get(i);
            if (s.probability > 0) {
                entropy -= s.probability * (Math.log(s.probability) / Math.log(2)); // Formula za Entropija
                avgLength += s.probability * s.code.length(); // Formula za prosecna dolzina
            }
            if (fullyDone) {
                tableModel.addRow(new Object[]{s.name, s.probability, s.code, s.code.length()});
            } else {
                tableModel.addRow(new Object[]{s.name, s.probability, "Во тек...", "-"});
            }
        }

        try {
            int b = Integer.parseInt(txtBaseB.getText().trim());
            // Efikanost spored teoremata na Shannon za kodiranje na izvor
            double efficiency = (entropy / (avgLength * (Math.log(b)/Math.log(2)))) * 100;
            if (fullyDone) {
                lblStats.setText(String.format("Ентропија H(X) = %.4f | Просечна должина L = %.4f | Ефикасност η = %.2f%%", 
                    entropy, avgLength, Math.min(efficiency, 100.0)));
            } else {
                lblStats.setText("Чекор: " + (currentStepIndex + 1) + " / " + stepNodesToDraw.size() + " визуелизација...");
            }
        } catch(Exception ignored) {}
    }

    /**
      CUSTOM PANEL KLASA: GraphicsPanel
      Vnatresna klasa koja nasleduva od JPanel.
      Ovozmozuva Zoom-in/Zoom-out so skrolot na gluvce i vlechenje (Drag & Drop) na crtezot na site strani.
     */
    private static class GraphicsPanel extends JPanel {
        private List<Node> nodes = new ArrayList<>();
        private List<VisualLine> lines = new ArrayList<>();

        private double zoomFactor = 1.0;
        private double offsetX = 0.0;
        private double offsetY = 0.0;
        private Point mousePt;

        public GraphicsPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240), 2));

            // Detekcija na klik so gluvce (pocetok na vlecenje)
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    mousePt = e.getPoint();
                }
            });

            // Vlecenje (pomestuvanje/paniranje na grafikata)
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (mousePt != null) {
                        offsetX += e.getX() - mousePt.x;
                        offsetY += e.getY() - mousePt.y;
                        mousePt = e.getPoint();
                        repaint(); // Prikazi go pak panelot za da se vidi pomestuvanjeto
                    }
                }
            });

            // Zumiranje
            addMouseWheelListener(e -> {
                double zoomDivisor = 1.1;
                if (e.getWheelRotation() < 0) {
                    zoomFactor *= zoomDivisor; // Zoom in
                } else {
                    zoomFactor /= zoomDivisor; // Zoom out
                }
                zoomFactor = Math.max(0.2, Math.min(zoomFactor, 4.0)); // Ogranici go zoom-ot od 20% do 400%
                repaint();
            });
        }

        public void setGraphContent(List<Node> nodes, List<VisualLine> lines) {
            this.nodes = nodes;
            this.lines = lines;
            this.zoomFactor = 1.0; // Resetiraj zoom na nov vcitaj
            this.offsetX = 0.0;
            this.offsetY = 0.0;
            repaint();
        }

        /**
         GLAVEN METOD ZA CRTANJE: paintComponent
         Se povikuva avtomatski od Java Swing koga i da treba da se osvezi ekranot.
         koristi Graphics2D za iscrtuvanje na linii, krugovi (zaobleni pravoagolnici) i tekstovi.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            // Vkluci Antialiasing za liniite i tekstovite da ne bidat "pikselizirani" i iskršeni 
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            java.awt.geom.AffineTransform oldTransform = g2.getTransform();

            // Primenat na transformaciite za PAN (pomestuvanje) i ZOOM
            g2.translate(offsetX, offsetY);
            g2.scale(zoomFactor, zoomFactor);

            // 1. CRTANJE NA LINIITE (Granki megu jazlite)
            g2.setStroke(new BasicStroke(1.4f)); // Debelina na linija
            g2.setColor(new Color(185, 195, 205));
            for (VisualLine line : lines) {
                g2.drawLine(line.x1, line.y1, line.x2, line.y2);
                // Ako linijata ima oznaka (0 ili 1), iscrtai ja tocno na sredina pomegu dvete tacki
                if (line.label != null && !line.label.isEmpty()) {
                    g2.setFont(new Font("Arial", Font.BOLD, 11));
                    g2.setColor(new Color(192, 57, 43)); // Crvena boja za bitovite
                    g2.drawString(line.label, (line.x1 + line.x2) / 2 - 3, (line.y1 + line.y2) / 2 - 2);
                    g2.setColor(new Color(185, 195, 205));
                }
            }

            // 2. CRTANJE NA JAZLITE (Meurcinja)
            for (Node node : nodes) {
                if (node.text.startsWith("dummy")) continue; // Preskokni gi nevidlivite jazli

                g2.setFont(new Font("Arial", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                
                // Dinamicka presmetka na goleminata na meurcheto spored dolzinata na tekstot
                int textWidth = fm.stringWidth(node.text.contains("<html>") ? "α1 α2 α3 α4" : node.text);
                int rw = Math.max(textWidth + 24, 45); 
                int rh = node.text.contains("<html>") ? 50 : 28; 

                // Razlicni boi za listovi (Krajni bukvi - Zelena) i vnatresni jazli (Sina)
                if (node.isLeaf) {
                    g2.setColor(new Color(238, 249, 242)); // Svetlo zelena vnatresnost
                    g2.fillRoundRect(node.x - rw / 2, node.y - rh / 2, rw, rh, 15, 15);
                    g2.setColor(new Color(46, 204, 113));  // Temno zelen rab
                    g2.drawRoundRect(node.x - rw / 2, node.y - rh / 2, rw, rh, 15, 15);
                } else {
                    g2.setColor(new Color(244, 248, 254)); // Svetlo sina vnatresnost
                    g2.fillRoundRect(node.x - rw / 2, node.y - rh / 2, rw, rh, 15, 15);
                    g2.setColor(new Color(52, 152, 219));  // Temno sin rab
                    g2.drawRoundRect(node.x - rw / 2, node.y - rh / 2, rw, rh, 15, 15);
                }

                g2.setColor(Color.BLACK);
                // Ako tekstot sodrzi HTML (se koristi kaj Shannon binaren i Gilbert-Moore za nov red)
                if (node.text.contains("<html>")) {
                    JLabel label = new JLabel(node.text, SwingConstants.CENTER);
                    label.setFont(new Font("Arial", Font.PLAIN, 10));
                    label.setSize(rw, rh);
                    g2.translate(node.x - rw / 2, node.y - rh / 2);
                    label.paint(g2); // Iskoristi go Swing rendererot za HTML tekst
                    g2.translate(-(node.x - rw / 2), -(node.y - rh / 2));
                } else {
                    // Standardno iscrtuvanje na obicen tekst vo centarot na meurcheto
                    int tx = node.x - fm.stringWidth(node.text) / 2;
                    int ty = node.y + fm.getAscent() / 2 - 2;
                    g2.drawString(node.text, tx, ty);
                }
            }

            g2.setTransform(oldTransform); // Vrati ja prvicnata sostojba na transformacija
        }
    }

    /**
      GLAVEN METOD (main)
      Ja startuva aplikacijata preku SwingUtilities.invokeLater so cel da se osigura
      deka celoto GUI se izvrsuva na edinstvenata bezbedna niza za grafika.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App_za_KODOVI().setVisible(true));
    }
}
