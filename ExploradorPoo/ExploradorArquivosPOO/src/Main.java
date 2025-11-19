// Imports simples, pois Main está no <default package>
import Modelo.GerenciadorMidia;
import Controle.MidiaController;
import Visao.TelaPrincipal;

public class Main {

    public static void main(String[] args) {
        
        /* Define a aparência (Look and Feel) para "Nimbus" */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        java.awt.EventQueue.invokeLater(() -> {
            
            GerenciadorMidia gerenciador = new GerenciadorMidia();
            MidiaController controlador = new MidiaController(gerenciador);
            TelaPrincipal tela = new TelaPrincipal();
            
         
            
            tela.setVisible(true);
        });
    }
}