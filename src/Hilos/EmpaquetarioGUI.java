/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Hilos;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
/**
 *
 * @author Ian Suazo Palao
 */
public class EmpaquetarioGUI extends JFrame{
    private final Logistica zona_r=new Logistica("Recepcion",10);
    private final Logistica zona_alm=new Logistica("Almacen",20);
    private final Logistica zona_clas=new Logistica("Clasificacion",10);
    private final Logistica zona_emp=new Logistica("Empaquetado",8);
    private final Logistica zona_exp=new Logistica("Expedicion",15);
    private final ListaEnlazadas<Paquete> lista_ent=new ListaEnlazadas<>();
    private final ListaEnlazadas<Paquete> lista_return=new ListaEnlazadas<>();
    
    private ControladorDeSimulacion control;
    private JTextArea txtlog;
    private JProgressBar pbrecep,pbalm,pbclas,pbemp,pbexp;
    private JTextArea lbldetrecep,lbldetalm,lbldetclas,lbldetemp,lbldetexp;
    private JPanel p_rep;
    private JLabel lblstatus;
    
    private HiloRecepcion hiloRecepcion;
    private HiloClasificador[] clasificadores;
    private HiloEmpaquetador[] empaquetadores;
    private HiloRepartidor[] repartidores;
    private Timer timerGUI;
    
    private JLabel[] lblRepartidorEstado;
    private JLabel[] lblRepartidorCarga;
    
    public EmpaquetarioGUI(){
        super("Empaqueteria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 850);
        setLocationRelativeTo(null);
        inicializarUI();
    }
    
    private void inicializarUI(){
        JPanel p_principal=new JPanel(new BorderLayout(6,8));
        p_principal.setBorder(new EmptyBorder(8,8,8,8));
        
        JPanel p_superior=new JPanel(new BorderLayout(5,5));
        JLabel lbltitulo=new JLabel("SISTEMA AUTOMATIZADO DE GESTION DE PAQUETERIA", JLabel.CENTER);
        lbltitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        p_superior.add(lbltitulo,BorderLayout.NORTH);
        
        JPanel panelBotones =new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton btnIniciar =new JButton("INICIAR");
        JButton btnPausar =new JButton("PAUSAR");
        JButton btnReanudar =new JButton("REANUDAR");
        JButton btnDetener =new JButton("DETENER");
        JButton btnReiniciar =new JButton("REINICIAR");
        JButton btnEstadisticas =new JButton("ESTADÍSTICAS");
        
        panelBotones.add(btnIniciar);
        panelBotones.add(btnPausar);
        panelBotones.add(btnReanudar);
        panelBotones.add(btnDetener);
        panelBotones.add(btnReiniciar);
        panelBotones.add(btnEstadisticas);
        p_superior.add(panelBotones, BorderLayout.SOUTH);
        p_principal.add(p_superior, BorderLayout.NORTH);
        
        JPanel p_central=new JPanel(new GridLayout(3,1,6,6));
        JPanel fila1=new JPanel(new GridLayout(1,3,6,6));
        fila1.add(crearPanelZona("RECEPCIÓN", zona_r, pbrecep=new JProgressBar(0,10), lbldetrecep=new JTextArea(4,15)));
        fila1.add(crearPanelZona("ALMACÉN", zona_alm, pbalm=new JProgressBar(0,20), lbldetalm=new JTextArea(4,15)));
        fila1.add(crearPanelZona("CLASIFICACIÓN", zona_clas, pbclas=new JProgressBar(0,10), lbldetclas=new JTextArea(4,15)));
        p_central.add(fila1);
        
        JPanel fila2=new JPanel(new GridLayout(1,2,6,6));
        fila2.add(crearPanelZona("EMPAQUETADO", zona_emp, pbemp=new JProgressBar(0, 8), lbldetemp=new JTextArea(4, 20)));
        fila2.add(crearPanelZona("EXPEDICIÓN (Rutas 1 a 4)", zona_exp, pbexp=new JProgressBar(0, 15), lbldetexp=new JTextArea(4, 20)));
        p_central.add(fila2);
        
        p_rep = new JPanel(new GridLayout(1,4,6,6));
        p_rep.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "FLOTA DE REPARTIDORES", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));
        lblRepartidorEstado = new JLabel[4];
        lblRepartidorCarga = new JLabel[4];

        String[] rutasNombres={"Ruta 1 (Centro)", "Ruta 2 (Gràcia)", "Ruta 3 (Sant Martí)", "Ruta 4 (Badalona)"};
        int[] caps = {5,4,6,5};
        
        for (int i = 0; i < 4; i++) {
            JPanel card = new JPanel(new GridLayout(4, 1, 2, 2));
            card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            JLabel lblId = new JLabel("Repartidor " + (i + 1) + " (" + rutasNombres[i] + ")", JLabel.CENTER);
            lblId.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lblRepartidorEstado[i] = new JLabel("Estado: DISPONIBLE", JLabel.CENTER);
            lblRepartidorCarga[i] = new JLabel("Carga: 0 / " + caps[i], JLabel.CENTER);
            JLabel lblCapMax = new JLabel("Capacidad Máx: " + caps[i], JLabel.CENTER);

            card.add(lblId);
            card.add(lblRepartidorEstado[i]);
            card.add(lblRepartidorCarga[i]);
            card.add(lblCapMax);
            p_rep.add(card);
        }
        p_central.add(p_rep);
        
        p_principal.add(p_central, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout(4, 4));
        panelInferior.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "REGISTRO DEL SISTEMA (LOG)", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        txtlog = new JTextArea(8, 80);
        txtlog.setEditable(false);
        txtlog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtlog.setBackground(new Color(248, 249, 250));
        JScrollPane scrollLog = new JScrollPane(txtlog);
        panelInferior.add(scrollLog, BorderLayout.CENTER);

        lblstatus = new JLabel("  Estado: LISTO PARA INICIAR  |  Generados: 0  |  Entregados: 0  |  Devueltos: 0  |  En Proceso: 0");
        lblstatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblstatus.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panelInferior.add(lblstatus, BorderLayout.SOUTH);

        p_principal.add(panelInferior, BorderLayout.SOUTH);

        setContentPane(p_principal);
        control=new ControladorDeSimulacion(txtlog);

        btnIniciar.addActionListener(e -> iniciarSimulacion());
        btnPausar.addActionListener(e -> control.pausar());
        btnReanudar.addActionListener(e -> control.reanudar());
        btnDetener.addActionListener(e -> detenerSimulacion());
        btnReiniciar.addActionListener(e -> reiniciarSimulacion());
        btnEstadisticas.addActionListener(e -> mostrarVentanaEstadisticas());

        timerGUI = new Timer(200, e -> refrescarUI());
        timerGUI.start();
    }
    
    private JPanel crearPanelZona(String titulo, Logistica zona, JProgressBar pb, JTextArea txtDetalle) {
        JPanel p=new JPanel(new BorderLayout(4,4));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), titulo+" (Cap: "+zona.getCapacidadMaxima()+")", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        pb.setStringPainted(true);
        p.add(pb,BorderLayout.NORTH);

        txtDetalle.setEditable(false);
        txtDetalle.setFont(new Font("Monospaced", Font.PLAIN,11));
        txtDetalle.setBackground(new Color(252,252,252));
        JScrollPane scroll=new JScrollPane(txtDetalle);
        p.add(scroll,BorderLayout.CENTER);
        return p;
    }
    
    private void iniciarSimulacion() {
        if (hiloRecepcion != null && hiloRecepcion.isAlive()) {
            JOptionPane.showMessageDialog(this, "La simulación ya está en ejecución.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        control.reiniciar();
        control.registrarLog("--- INICIANDO PROCESOS DE LOGÍSTICA ---");

        hiloRecepcion = new HiloRecepcion(zona_r, zona_alm, control);

        clasificadores = new HiloClasificador[3];
        for (int i = 0; i < 3; i++) {
            clasificadores[i] = new HiloClasificador("Clasificador-" + (i + 1), zona_alm, zona_clas, zona_emp, control);
        }

        empaquetadores = new HiloEmpaquetador[2];
        for (int i = 0; i < 2; i++) {
            empaquetadores[i] = new HiloEmpaquetador("Empaquetador-" + (i + 1), zona_emp, zona_exp, control);
        }

        repartidores = new HiloRepartidor[4];
        String[] rutas = {"Ruta 1", "Ruta 2", "Ruta 3", "Ruta 4"};
        int[] capacidades = {5, 4, 6, 5};
        for (int i = 0; i < 4; i++) {
            repartidores[i] = new HiloRepartidor("R" + (i + 1), "Repartidor-" + (i + 1), capacidades[i], rutas[i],zona_exp, lista_ent, lista_return, control);
        }

        hiloRecepcion.start();
        for (HiloClasificador c : clasificadores) c.start();
        for (HiloEmpaquetador emp : empaquetadores) emp.start();
        for (HiloRepartidor r : repartidores) r.start();
    }
    
    private void detenerSimulacion() {
        control.detener();
    }

    private void reiniciarSimulacion() {
        control.detener();
        zona_r.vaciar();
        zona_alm.vaciar();
        zona_clas.vaciar();
        zona_emp.vaciar();
        zona_exp.vaciar();
        lista_ent.limpiar();
        lista_return.limpiar();
        txtlog.setText("");
        control.reiniciar();
        refrescarUI();
    }
    
    private void refrescarUI(){
        actualizarBarraYZona(pbrecep, lbldetrecep, zona_r);
        actualizarBarraYZona(pbalm, lbldetalm, zona_alm);
        actualizarBarraYZona(pbclas, lbldetclas, zona_clas);
        actualizarBarraYZona(pbemp, lbldetemp, zona_emp);
        actualizarBarraYZona(pbexp, lbldetexp, zona_exp);
        
        if (clasificadores != null && clasificadores.length == 3) {
            StringBuilder sb = new StringBuilder();
            for (HiloClasificador c : clasificadores) {
                sb.append(c.getEstadoVisual()).append("\n");
            }
            lbldetclas.setText(sb.toString());
        }

        if (empaquetadores != null && empaquetadores.length == 2) {
            StringBuilder sb = new StringBuilder();
            for (HiloEmpaquetador emp : empaquetadores) {
                sb.append(emp.getEstadoVisual()).append("\n");
            }
            lbldetemp.setText(sb.toString());
        }

        if (repartidores!=null){
            for (int i = 0; i < repartidores.length; i++) {
                if (repartidores[i]!=null){
                    lblRepartidorEstado[i].setText("Estado: " + repartidores[i].getEstado().name());
                    lblRepartidorCarga[i].setText(String.format("Carga: %d / %d | Entregas: %d",repartidores[i].getPaquetesActuales(), repartidores[i].getCapacidad(), repartidores[i].getEntregadosCount()));
                }
            }
        }

        lblstatus.setText(String.format("  Generados: %d  |  Entregados: %d  |  Devueltos: %d  |  En Proceso: %d  |  Pausado: %s",control.getPaquetesGenerados(),control.getPaquetesEntregados(),control.getPaquetesDevueltos(),control.getPaquetesEnProceso(),control.estaPausado() ? "SÍ" : "NO"));
    }
    
    private void actualizarBarraYZona(JProgressBar pb, JTextArea txt, Logistica zona) {
        int ocupacion=zona.getOcupacion();
        int max=zona.getCapacidadMaxima();
        pb.setMaximum(max);
        pb.setValue(ocupacion);
        pb.setString(ocupacion+" / "+max+" paquetes");

        if (zona!=zona_clas && zona!=zona_emp){
            txt.setText(zona.getResumen(12));
        }
    }
    
    private void mostrarVentanaEstadisticas() {
        JDialog dlg=new JDialog(this, "📊 Panel de Estadísticas del Sistema", true);
        dlg.setSize(450, 480);
        dlg.setLocationRelativeTo(this);

        JPanel panel=new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextArea txtStats=new JTextArea();
        txtStats.setEditable(false);
        txtStats.setFont(new Font("Monospaced", Font.PLAIN, 13));

        StringBuilder sb=new StringBuilder();
        sb.append("==========================================\n");
        sb.append("          ESTADÍSTICAS DEL CENTRO         \n");
        sb.append("==========================================\n\n");
        sb.append("Paquetes generados:      ").append(control.getPaquetesGenerados()).append("\n");
        sb.append("Entregados exitosos:     ").append(control.getPaquetesEntregados()).append("\n");
        sb.append("Devueltos (fallidos):    ").append(control.getPaquetesDevueltos()).append("\n");
        sb.append("En proceso logístico:    ").append(control.getPaquetesEnProceso()).append("\n");
        sb.append(String.format("Tiempo promedio entrega: %.2f s\n\n", control.getTiempoPromedio()));

        sb.append("--- RENDIMIENTO REPARTIDORES ---\n");
        if (repartidores != null){
            for (HiloRepartidor r : repartidores){
                sb.append(String.format("%-15s (%s): %d entregas\n", r.getNombre(), r.getRutaAsignada(), r.getEntregadosCount()));
            }
        }
        sb.append("\nTotal paquetes devueltos en lista: ").append(lista_return.contar()).append(" (Conteo)\n");

        txtStats.setText(sb.toString());
        panel.add(new JScrollPane(txtStats), BorderLayout.CENTER);

        JPanel pnlBtns=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExportar=new JButton("💾 Exportar a Archivo (.txt / .dat)");
        btnExportar.addActionListener(e -> {
            try {
                GestorDeArchivos.guardarReporte("Reporte_Simulacion.txt", control, lista_ent, lista_return);
                GestorDeArchivos.guardarBinario("Estadisticas.dat", control);
                JOptionPane.showMessageDialog(dlg, "Archivos 'Reporte_Simulacion.txt' y 'Estadisticas.dat' guardados exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error al guardar archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnCerrar=new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dlg.dispose());

        pnlBtns.add(btnExportar);
        pnlBtns.add(btnCerrar);
        panel.add(pnlBtns, BorderLayout.SOUTH);

        dlg.setContentPane(panel);
        dlg.setVisible(true);
    }

    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            EmpaquetarioGUI ventana=new EmpaquetarioGUI();
            ventana.setVisible(true);
        });
    }
            
}
