import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class AppGUI {
    private JFrame window;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    private Member activeMember;
    private boolean activeMemberBaru;
    private List<Menu> selectedItems = new ArrayList<>();
    private List<Integer> selectedQtys = new ArrayList<>();
    
    private JTextField txtNama;
    private JTextField txtSaldo;
    private JRadioButton rbTunai;
    private JRadioButton rbQris;
    private JRadioButton rbEmoney;
    private JComboBox<String> cbCurrency;
    private static Dapur dapur = new Dapur();
    private static int jumlahPelanggan = 0;

    public void run() {
        window = new JFrame("KohiSop POS System");
        window.setSize(850, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        window.add(cardPanel);

        showStep(1);
        window.setVisible(true);
    }

    private void showStep(int step) {
        JPanel stepPanel = null;
        if (step == 1) stepPanel = createStep1();
        else if (step == 2) stepPanel = createStep2();
        else if (step == 3) stepPanel = createStep3();
        else if (step == 4) stepPanel = createStep4();
        else if (step == 5) stepPanel = createStep5();

        cardPanel.removeAll();
        cardPanel.add(stepPanel, "step");
        cardLayout.show(cardPanel, "step");
        window.setTitle("KohiSop - Langkah " + step + " dari 5");
        window.revalidate();
        window.repaint();
    }

    private JPanel createStep1() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel lblHeader = new JLabel("Langkah 1 dari 5: Input Pelanggan", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblHeader, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3, 1, 10, 10));
        
        JPanel rowNama = new JPanel(new FlowLayout());
        rowNama.add(new JLabel("Nama Pelanggan:"));
        txtNama = new JTextField(txtNama == null ? "" : txtNama.getText(), 20);
        rowNama.add(txtNama);
        center.add(rowNama);

        JLabel lblStatus = new JLabel("Masukkan nama untuk mendeteksi status member.", SwingConstants.CENTER);
        center.add(lblStatus);

        JLabel lblNotice = new JLabel("Non-member otomatis didaftarkan setelah transaksi.", SwingConstants.CENTER);
        lblNotice.setFont(new Font("Arial", Font.ITALIC, 11));
        center.add(lblNotice);
        panel.add(center, BorderLayout.CENTER);

        Runnable checkMember = () -> {
            String name = txtNama.getText().trim();
            if (name.isEmpty()) {
                lblStatus.setText("Masukkan nama untuk mendeteksi status member.");
                lblStatus.setForeground(Color.BLACK);
            } else {
                Member m = KohiSop.cariMember(name);
                if (m != null) {
                    lblStatus.setText("Member: " + m.getKode() + " | Poin: " + m.getPoin());
                    lblStatus.setForeground(new Color(0, 128, 0));
                } else {
                    lblStatus.setText("Belum terdaftar sebagai member (Akan dibuat baru).");
                    lblStatus.setForeground(Color.BLUE);
                }
            }
        };
        
        txtNama.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { checkMember.run(); }
            public void removeUpdate(DocumentEvent e) { checkMember.run(); }
            public void changedUpdate(DocumentEvent e) { checkMember.run(); }
        });
        checkMember.run();

        JPanel footer = new JPanel(new FlowLayout());
        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> { txtNama.setText(""); checkMember.run(); });
        
        JButton btnLanjut = new JButton("Lanjut");
        btnLanjut.addActionListener(e -> {
            String name = txtNama.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(window, "Nama tidak boleh kosong!");
                return;
            }
            Member m = KohiSop.cariMember(name);
            if (m == null) {
                activeMemberBaru = true;
                String kodeBaru;
                while (true) {
                    kodeBaru = Member.generateRandomCode();
                    boolean exists = false;
                    for (Member existing : KohiSop.getDatabaseMember()) {
                        if (existing.getKode().equalsIgnoreCase(kodeBaru)) { exists = true; break; }
                    }
                    if (!exists) break;
                }
                activeMember = new Member(kodeBaru, name, 0);
            } else {
                activeMemberBaru = false;
                activeMember = m;
            }
            showStep(2);
        });

        footer.add(btnBatal);
        footer.add(btnLanjut);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createStep2() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblHeader = new JLabel("Langkah 2 dari 5: Pilih Menu", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblHeader, BorderLayout.NORTH);

        JPanel mainGrid = new JPanel(new GridLayout(1, 3, 10, 10));

        JPanel pFood = new JPanel(new BorderLayout());
        pFood.setBorder(BorderFactory.createTitledBorder("Makanan (Maks 5 jenis)"));
        JPanel listFood = new JPanel();
        listFood.setLayout(new BoxLayout(listFood, BoxLayout.Y_AXIS));

        JPanel pDrink = new JPanel(new BorderLayout());
        pDrink.setBorder(BorderFactory.createTitledBorder("Minuman (Maks 5 jenis)"));
        JPanel listDrink = new JPanel();
        listDrink.setLayout(new BoxLayout(listDrink, BoxLayout.Y_AXIS));

        JPanel pSelected = new JPanel(new BorderLayout());
        pSelected.setBorder(BorderFactory.createTitledBorder("Pesanan Sementara"));
        JPanel listSel = new JPanel();
        listSel.setLayout(new BoxLayout(listSel, BoxLayout.Y_AXIS));

        Runnable refreshSelectedList = new Runnable() {
            public void run() {
                listSel.removeAll();
                for (int i = 0; i < selectedItems.size(); i++) {
                    Menu m = selectedItems.get(i);
                    JPanel row = new JPanel(new BorderLayout(5, 5));
                    row.setMaximumSize(new Dimension(250, 30));
                    row.add(new JLabel(m.getKode() + " - " + m.getNamaMenu()), BorderLayout.CENTER);
                    JButton btnDel = new JButton("X");
                    btnDel.setMargin(new Insets(2, 5, 2, 5));
                    btnDel.addActionListener(e -> {
                        int idx = selectedItems.indexOf(m);
                        if (idx != -1) {
                            selectedItems.remove(idx);
                            selectedQtys.remove(idx);
                            run();
                        }
                    });
                    row.add(btnDel, BorderLayout.EAST);
                    listSel.add(row);
                }
                listSel.revalidate();
                listSel.repaint();
            }
        };

        for (Menu m : KohiSop.getDaftarMenu()) {
            JPanel row = new JPanel(new BorderLayout(5, 5));
            row.setMaximumSize(new Dimension(250, 30));
            row.add(new JLabel(String.format("%s - %s (Rp%,d)", m.getKode(), m.getNamaMenu(), m.getHarga())), BorderLayout.CENTER);
            JButton btnAdd = new JButton("Tambah");
            btnAdd.addActionListener(e -> {
                if (selectedItems.contains(m)) {
                    JOptionPane.showMessageDialog(window, m.getNamaMenu() + " sudah dipilih.");
                    return;
                }
                int count = 0;
                for (Menu s : selectedItems) {
                    if (s.getClass() == m.getClass()) count++;
                }
                if (count >= KohiSop.MAX_JENIS_PER_KATEGORI) {
                    JOptionPane.showMessageDialog(window, "Maksimal 5 jenis per kategori!");
                    return;
                }
                selectedItems.add(m);
                selectedQtys.add(1);
                refreshSelectedList.run();
            });
            row.add(btnAdd, BorderLayout.EAST);
            if (m instanceof Makanan) {
                listFood.add(row);
            } else {
                listDrink.add(row);
            }
        }

        pFood.add(new JScrollPane(listFood), BorderLayout.CENTER);
        pDrink.add(new JScrollPane(listDrink), BorderLayout.CENTER);
        pSelected.add(new JScrollPane(listSel), BorderLayout.CENTER);

        mainGrid.add(pFood);
        mainGrid.add(pDrink);
        mainGrid.add(pSelected);
        panel.add(mainGrid, BorderLayout.CENTER);

        refreshSelectedList.run();

        JPanel footer = new JPanel(new FlowLayout());
        JButton btnBack = new JButton("Kembali");
        btnBack.addActionListener(e -> showStep(1));
        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> { resetOrder(); showStep(1); });
        JButton btnNext = new JButton("Lanjut");
        btnNext.addActionListener(e -> {
            if (selectedItems.isEmpty()) {
                JOptionPane.showMessageDialog(window, "Pilih minimal 1 menu!");
                return;
            }
            showStep(3);
        });
        footer.add(btnBack);
        footer.add(btnBatal);
        footer.add(btnNext);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStep3() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblHeader = new JLabel("Langkah 3 dari 5: Kuantitas Pesanan", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblHeader, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        JLabel lblSubtotal = new JLabel("", SwingConstants.CENTER);
        lblSubtotal.setFont(new Font("Arial", Font.BOLD, 14));

        Runnable updateSubtotal = () -> {
            double total = 0;
            int count = 0;
            for (int i = 0; i < selectedItems.size(); i++) {
                int qty = selectedQtys.get(i);
                total += selectedItems.get(i).getHarga() * qty;
                count += qty;
            }
            lblSubtotal.setText(String.format("Jumlah item: %d | Subtotal: Rp%,.0f", count, total));
        };

        for (int i = 0; i < selectedItems.size(); i++) {
            final int idx = i;
            Menu m = selectedItems.get(i);
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
            row.setMaximumSize(new Dimension(800, 40));
            row.add(new JLabel(String.format("%s - %s (Rp%,d)", m.getKode(), m.getNamaMenu(), m.getHarga())));
            
            JButton btnMinus = new JButton("-");
            JLabel lblQty = new JLabel(String.valueOf(selectedQtys.get(idx)));
            JButton btnPlus = new JButton("+");
            
            btnMinus.addActionListener(e -> {
                int val = selectedQtys.get(idx);
                if (val > 1) {
                    selectedQtys.set(idx, val - 1);
                    lblQty.setText(String.valueOf(val - 1));
                    updateSubtotal.run();
                }
            });

            btnPlus.addActionListener(e -> {
                int val = selectedQtys.get(idx);
                if (val < m.getMaxKuantitas()) {
                    selectedQtys.set(idx, val + 1);
                    lblQty.setText(String.valueOf(val + 1));
                    updateSubtotal.run();
                } else {
                    JOptionPane.showMessageDialog(window, "Maksimal porsi untuk " + m.getNamaMenu() + " adalah " + m.getMaxKuantitas());
                }
            });

            row.add(btnMinus);
            row.add(lblQty);
            row.add(btnPlus);
            row.add(new JLabel("(Maks: " + m.getMaxKuantitas() + ")"));
            listPanel.add(row);
        }

        updateSubtotal.run();

        JPanel center = new JPanel(new BorderLayout(5, 5));
        center.add(new JScrollPane(listPanel), BorderLayout.CENTER);
        center.add(lblSubtotal, BorderLayout.SOUTH);
        panel.add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout());
        JButton btnBack = new JButton("Kembali");
        btnBack.addActionListener(e -> showStep(2));
        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> { resetOrder(); showStep(1); });
        JButton btnNext = new JButton("Lanjut");
        btnNext.addActionListener(e -> showStep(4));
        footer.add(btnBack);
        footer.add(btnBatal);
        footer.add(btnNext);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStep4() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblHeader = new JLabel("Langkah 4 dari 5: Pembayaran", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblHeader, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(1, 2, 20, 20));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createTitledBorder("Channel Pembayaran & Saldo"));

        if (rbTunai == null) {
            rbTunai = new JRadioButton("Tunai", true);
            rbQris = new JRadioButton("QRIS (Diskon 5%)");
            rbEmoney = new JRadioButton("eMoney (Diskon 7% + Admin Rp20)");
            ButtonGroup bg = new ButtonGroup();
            bg.add(rbTunai); bg.add(rbQris); bg.add(rbEmoney);
            txtSaldo = new JTextField("0", 10);
            cbCurrency = new JComboBox<>(new String[]{"IDR", "USD", "JPY", "MYR", "EUR"});
        }

        left.add(rbTunai);
        left.add(rbQris);
        left.add(rbEmoney);

        JPanel rowSaldo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rowSaldo.add(new JLabel("Saldo:"));
        rowSaldo.add(txtSaldo);
        left.add(rowSaldo);

        JLabel lblVerif = new JLabel("Pembayaran tunai tidak perlu cek saldo.");
        left.add(lblVerif);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createTitledBorder("Mata Uang & Kurs"));
        right.add(new JLabel("Pilih Mata Uang:"));
        right.add(cbCurrency);
        right.add(Box.createVerticalStrut(10));
        right.add(new JLabel("Kurs Konversi:"));
        right.add(new JLabel("- 1 USD = Rp 15"));
        right.add(new JLabel("- 10 JPY = Rp 1"));
        right.add(new JLabel("- 1 MYR = Rp 4"));
        right.add(new JLabel("- 1 EUR = Rp 14"));

        body.add(left);
        body.add(right);
        panel.add(body, BorderLayout.CENTER);

        JPanel sumPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JLabel lblSumSub = new JLabel("");
        JLabel lblSumDisc = new JLabel("");
        JLabel lblSumAdmin = new JLabel("");
        sumPanel.add(lblSumSub);
        sumPanel.add(lblSumDisc);
        sumPanel.add(lblSumAdmin);
        panel.add(sumPanel, BorderLayout.EAST);

        Runnable updateSummary = () -> {
            boolean isTunai = rbTunai.isSelected();
            txtSaldo.setEnabled(!isTunai);
            
            double subtotal = 0;
            for (int i = 0; i < selectedItems.size(); i++) {
                subtotal += selectedItems.get(i).getHarga() * selectedQtys.get(i);
            }

            boolean bebasPajak = !activeMemberBaru && activeMember.getKode().toUpperCase().contains("A");
            double tax = 0;
            for (int i = 0; i < selectedItems.size(); i++) {
                Menu menu = selectedItems.get(i);
                int qty = selectedQtys.get(i);
                tax += Pajak.hitungPajak(menu, menu.getHarga() * qty, bebasPajak);
            }

            double totalDenganPajak = subtotal + tax;
            double discPercent = rbQris.isSelected() ? 0.05 : (rbEmoney.isSelected() ? 0.07 : 0.0);
            double admin = rbEmoney.isSelected() ? 20 : 0;
            double diskon = totalDenganPajak * discPercent;
            double totalSebelumPoin = totalDenganPajak - diskon + admin;

            double potonganPoin = 0;
            if (!activeMemberBaru) {
                int poinSebelum = activeMember.getPoin();
                double nilaiPoin = poinSebelum * 2.0;
                potonganPoin = Math.min(nilaiPoin, totalSebelumPoin);
            }
            double totalAkhir = totalSebelumPoin - potonganPoin;

            lblSumSub.setText(String.format("Subtotal: Rp%,.0f (Pajak: Rp%,.0f)", subtotal, tax));
            lblSumDisc.setText(String.format("Diskon Channel/Poin: Rp%,.0f", diskon + potonganPoin));
            lblSumAdmin.setText(String.format("Biaya Admin: Rp%,.0f | Tagihan Akhir: Rp%,.0f", admin, totalAkhir));

            if (isTunai) {
                lblVerif.setText("Pembayaran tunai tidak perlu cek saldo.");
                lblVerif.setForeground(Color.BLACK);
            } else {
                try {
                    double saldo = Double.parseDouble(txtSaldo.getText().trim());
                    if (saldo >= totalAkhir) {
                        lblVerif.setText("Saldo mencukupi");
                        lblVerif.setForeground(new Color(0, 128, 0));
                    } else {
                        lblVerif.setText("Saldo tidak mencukupi!");
                        lblVerif.setForeground(Color.RED);
                    }
                } catch (NumberFormatException ex) {
                    lblVerif.setText("Masukkan saldo angka yang valid.");
                    lblVerif.setForeground(Color.RED);
                }
            }
        };

        ActionListener listener = e -> updateSummary.run();
        rbTunai.addActionListener(listener);
        rbQris.addActionListener(listener);
        rbEmoney.addActionListener(listener);
        txtSaldo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateSummary.run(); }
            public void removeUpdate(DocumentEvent e) { updateSummary.run(); }
            public void changedUpdate(DocumentEvent e) { updateSummary.run(); }
        });
        updateSummary.run();

        JPanel footer = new JPanel(new FlowLayout());
        JButton btnBack = new JButton("Kembali");
        btnBack.addActionListener(e -> showStep(3));
        JButton btnBatal = new JButton("Batal");
        btnBatal.addActionListener(e -> { resetOrder(); showStep(1); });
        
        JButton btnNext = new JButton("Lanjut");
        btnNext.addActionListener(e -> {
            if (!rbTunai.isSelected()) {
                try {
                    double saldo = Double.parseDouble(txtSaldo.getText().trim());
                    double subtotal = 0;
                    for (int i = 0; i < selectedItems.size(); i++) {
                        subtotal += selectedItems.get(i).getHarga() * selectedQtys.get(i);
                    }
                    boolean bebasPajak = !activeMemberBaru && activeMember.getKode().toUpperCase().contains("A");
                    double tax = 0;
                    for (int i = 0; i < selectedItems.size(); i++) {
                        Menu menu = selectedItems.get(i);
                        int qty = selectedQtys.get(i);
                        tax += Pajak.hitungPajak(menu, menu.getHarga() * qty, bebasPajak);
                    }
                    double totalDenganPajak = subtotal + tax;
                    double discPercent = rbQris.isSelected() ? 0.05 : (rbEmoney.isSelected() ? 0.07 : 0.0);
                    double admin = rbEmoney.isSelected() ? 20 : 0;
                    double diskon = totalDenganPajak * discPercent;
                    double totalSebelumPoin = totalDenganPajak - diskon + admin;
                    double potonganPoin = 0;
                    if (!activeMemberBaru) {
                        potonganPoin = Math.min(activeMember.getPoin() * 2.0, totalSebelumPoin);
                    }
                    double totalAkhir = totalSebelumPoin - potonganPoin;
                    if (saldo < totalAkhir) {
                        JOptionPane.showMessageDialog(window, "Saldo tidak mencukupi!");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(window, "Masukkan saldo yang valid!");
                    return;
                }
            }
            showStep(5);
        });

        footer.add(btnBack);
        footer.add(btnBatal);
        footer.add(btnNext);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStep5() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblHeader = new JLabel("Langkah 5 dari 5: Tagihan & Kuitansi", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblHeader, BorderLayout.NORTH);

        KohiSop kohiSop = new KohiSop();
        for (int i = 0; i < selectedItems.size(); i++) {
            kohiSop.tambahPesanan(selectedItems.get(i), selectedQtys.get(i));
        }
        kohiSop.urutkanPesanan();

        ChannelPembayaran channel = rbQris.isSelected() ? new QRIS() : (rbEmoney.isSelected() ? new Emoney() : new Tunai());
        
        MataUang mataUang;
        int curIdx = cbCurrency.getSelectedIndex();
        if (curIdx == 1) mataUang = new USD();
        else if (curIdx == 2) mataUang = new JPY();
        else if (curIdx == 3) mataUang = new MYR();
        else if (curIdx == 4) mataUang = new EUR();
        else mataUang = new IDR();

        double totalHargaLuarPajak = 0;
        double totalPajakKeseluruhan = 0;
        boolean bebasPajak = !activeMemberBaru && activeMember.getKode().toUpperCase().contains("A");
        for (int i = 0; i < kohiSop.getJumlahPesanan(); i++) {
            ItemPesanan ip = kohiSop.getPesanan(i);
            ip.setBebasPajak(bebasPajak);
            totalHargaLuarPajak += ip.getTotalHarga();
            totalPajakKeseluruhan += ip.getPajak();
        }

        double totalHargaDenganPajak = totalHargaLuarPajak + totalPajakKeseluruhan;
        double diskon = channel.hitungDiskon(totalHargaDenganPajak);
        double admin = channel.getBiayaAdmin();
        double totalSebelumPoin = totalHargaDenganPajak - diskon + admin;

        int poinSebelum = activeMember.getPoin();
        int poinYangDigunakan = 0;
        double potonganPoin = 0;

        if (!activeMemberBaru) {
            double nilaiPoin = poinSebelum * 2.0;
            if (nilaiPoin >= totalSebelumPoin) {
                potonganPoin = totalSebelumPoin;
                poinYangDigunakan = (int) Math.ceil(totalSebelumPoin / 2.0);
            } else {
                potonganPoin = nilaiPoin;
                poinYangDigunakan = poinSebelum;
            }
        }

        double totalAkhirIDR = totalSebelumPoin - potonganPoin;
        int poinDiperoleh = (int) (totalAkhirIDR / 10);
        if (activeMember.getKode().toUpperCase().contains("A")) {
            poinDiperoleh *= 2;
        }

        int poinSetelah = poinSebelum - poinYangDigunakan + poinDiperoleh;

        JPanel summaryPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Ringkasan Tagihan"));
        summaryPanel.add(new JLabel("Nama Pelanggan: " + activeMember.getNama()));
        summaryPanel.add(new JLabel("Kode Member: " + activeMember.getKode() + (activeMemberBaru ? " (Baru)" : "")));
        summaryPanel.add(new JLabel("Poin Sebelum: " + poinSebelum + " | Diperoleh: " + poinDiperoleh));
        summaryPanel.add(new JLabel("Poin Setelah: " + poinSetelah));
        summaryPanel.add(new JLabel(String.format("Total Sebelum Pajak: Rp%,.0f", totalHargaLuarPajak)));
        summaryPanel.add(new JLabel(String.format("Pajak: Rp%,.0f | Potongan Poin: Rp%,.0f", totalPajakKeseluruhan, potonganPoin)));
        summaryPanel.add(new JLabel(String.format("Diskon Channel: Rp%,.0f | Admin: Rp%,.0f", diskon, admin)));
        summaryPanel.add(new JLabel(String.format("TOTAL AKHIR (IDR): Rp%,.0f", totalAkhirIDR)));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream oldOut = System.out;
        System.setOut(ps);
        
        activeMember.setPoin(poinSetelah);
        Kuitansi.cetak(kohiSop, channel, mataUang, activeMember);

        System.out.flush();
        System.setOut(oldOut);
        String receiptText = baos.toString();

        JTextArea txtReceipt = new JTextArea(receiptText);
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font("Consolas", Font.PLAIN, 12));

        JPanel body = new JPanel(new GridLayout(1, 2, 10, 10));
        body.add(summaryPanel);
        body.add(new JScrollPane(txtReceipt));
        panel.add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout());
        JButton btnBack = new JButton("Kembali");
        btnBack.addActionListener(e -> {
            activeMember.setPoin(poinSebelum);
            showStep(4);
        });
        
        JButton btnCetak = new JButton("Cetak Kuitansi");
        btnCetak.addActionListener(e -> JOptionPane.showMessageDialog(window, "Kuitansi telah dicetak!"));

        JButton btnSelesai = new JButton("Selesai");
        btnSelesai.addActionListener(e -> {
            if (activeMemberBaru) {
                KohiSop.daftarMemberBaru(activeMember);
            } else {
                KohiSop.saveDatabaseMember();
            }

            // dapur
            jumlahPelanggan++;
            dapur.tambahPesanan(kohiSop.getDaftarPesanan());

            if (jumlahPelanggan % 3 != 0) {
                JOptionPane.showMessageDialog(window,
                    String.format("Tim dapur menunggu pesanan lainnya... (%d/3 pesanan)", jumlahPelanggan % 3));
            } else {
                int choice = JOptionPane.showConfirmDialog(window,
                    "Pesanan 3 pelanggan sudah terkumpul!\nLihat proses tim dapur?",
                    "Proses Tim Dapur",
                    JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    ByteArrayOutputStream baosDapur = new ByteArrayOutputStream();
                    PrintStream psDapur = new PrintStream(baosDapur);
                    PrintStream oldOutDapur = System.out;
                    System.setOut(psDapur);
                    
                    dapur.prosesPesanan();
                    
                    System.out.flush();
                    System.setOut(oldOutDapur);
                    String dapurText = baosDapur.toString();

                    JTextArea txtDapur = new JTextArea(dapurText);
                    txtDapur.setEditable(false);
                    txtDapur.setFont(new Font("Consolas", Font.PLAIN, 12));
                    JScrollPane scroll = new JScrollPane(txtDapur);
                    scroll.setPreferredSize(new Dimension(600, 400));
                    JOptionPane.showMessageDialog(window, scroll, "Tim Dapur KohiSop", JOptionPane.INFORMATION_MESSAGE);
                }
                dapur.kosongkanAntrian();
            }

            resetOrder();
            showStep(1);
        });

        footer.add(btnBack);
        footer.add(btnCetak);
        footer.add(btnSelesai);
        panel.add(footer, BorderLayout.SOUTH);

        return panel;
    }

    private void resetOrder() {
        if (txtNama != null) txtNama.setText("");
        selectedItems.clear();
        selectedQtys.clear();
        if (txtSaldo != null) txtSaldo.setText("0");
        if (rbTunai != null) rbTunai.setSelected(true);
        if (cbCurrency != null) cbCurrency.setSelectedIndex(0);
        activeMember = null;
        activeMemberBaru = false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppGUI().run());
    }
}
