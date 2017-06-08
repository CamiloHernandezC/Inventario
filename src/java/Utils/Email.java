/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import Entities.Notificaciones;
import Entities.PersonasSucursal;
import Entities.Usuarios;
import java.io.File;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author MAURICIO
 */
public class Email implements Runnable{    
    
    //final String senderEmailID = "cager@seracis.com"; //Correo emisor 
    //final String senderPassword = "Veryown2015";  // PW emisor 
    //final String emailSMTPserver = "mail.seracis.com"; //Servidor SMTP 
    
    //final String senderEmailID = "camilo.hernandez.castillo@gmail.com"; //Correo emisor 
    //final String senderPassword = "KMILO_15";  // PW emisor 
    //final String emailSMTPserver = "smtp.gmail.com"; //Servidor SMTP 
    
    private static final String senderEmailID = "controlacceso.cager@gmail.com"; //Correo emisor 
    private static final String senderPassword = "Colombia2015*";  // PW emisor 
    private static final String emailSMTPserver = "smtp.gmail.com"; //Servidor SMTP 
    private static final String emailServerPort = "465"; // SSL
    private static final Properties props = new Properties();
    private String receiverEmails;
    private String ccEmails;
    private String emailSubject;
    private String emailBody;
    private String rutaArchivo;

    private static String buildMessage(Notificaciones notification,Object object, String tipoEvento) {
        String empresaOrigen="";
        String ente="";
        String entidad="";
        String sucursal="";
        String id="";
        
        String message="";
        
        switch(tipoEvento){
            case Constants.STRING_ENTRY:
                message += "Acaba de entrar ";//TODO CRFEATE BUNDLE PROPERTY
                break;
            case Constants.STRING_EXIT:
                message += "Acaba de salir ";//TODO CRFEATE BUNDLE PROPERTY
                break;
                
        }
        
        if(object instanceof PersonasSucursal){
            PersonasSucursal persona = (PersonasSucursal) object;
            if(persona.getPersonas().getEmpresaOrigen()!=null){
                empresaOrigen=persona.getPersonas().getEmpresaOrigen().getNombre1();
            }
            ente = "la persona";
            entidad = "el "+persona.getEntidad().getDescripcion();
            sucursal = persona.getSucursales().getNombre();
            id = " " +persona.getPersonas().getNombre1() + " " + persona.getPersonas().getApellido1() +" identificado con "+ persona.getPersonas().getTipoDocumento().getDescripcion() + ": " + persona.getPersonas().getNumeroDocumento();
        }
        
        if(notification.getMostrarEntidad()){
            message+=entidad;
        }else{
            if(notification.getMostrarEnte()){
                message+=ente;
            }
        }
        message += id;
        if(notification.getMostrarEmpresaOrigen()){
            message+=" de la empresa "+empresaOrigen;
        }
        
        if(notification.getMostrarSucursal()){
            message +=" en la sucursal "+sucursal;
        }
        if(notification.getMostrarPorteria()){
            message += " por la porteria Prueba";//TODO ASSIGN REAL ENTRY HERE
        }
        message+=". \n"+notification.getMensaje();
        return message;
    }
    
    public Email(Notificaciones notification,String rutaArchivo, Object object, String tipoEvento) {
        receiverEmails = notification.getMail(); //Dato de receptor y mensaje por defecto 
        ccEmails = null;
        emailSubject = notification.getAsunto();
        emailBody = buildMessage(notification,object,tipoEvento);
        File f = new File(rutaArchivo);
        if(f.exists()) { 
            this.rutaArchivo = rutaArchivo;
        }else{
            this.rutaArchivo = null;
        }
        initProps();
    }
    
    public Email(String Nombre,String correo,String Descripcion,String ciudad) {
        receiverEmails="andresmaomorales@gmail.com";
        ccEmails = null;
        emailSubject = "Contacto al Servicio Tecnico";
        emailBody = new Date()+"\n"
                + " \n"
                + "Email enviado por: \n"
                + "\n"
                + Nombre +"\n"
                + "\n"
                + "ubicado en la ciudad de : \n"
                + "\n"
                + ciudad +"\n"
                + "\n"
                + "Donde la descripcion fue : \n"
                + "\n"
                + Descripcion +"\n"
                + "\n"
                + "Con direccion o telefono de contacto \n"
                + "\n"
                + correo +"\n"
                +"\n"
                + "Best regards, Support Team";
        initProps();
                
    }
    
    public Email(Usuarios user) {
        receiverEmails = user.getMail(); //Dato de receptor y mensaje por defecto 
        ccEmails = null;
        emailSubject = "Recover Password";//TODO CREATE BUNDLE PROPERTIES FOR STRINGS BELOW
        emailBody = new Date()+"\n"
                + " \n"
                + "Your requested password\n"
                + " \n"
                + "This is an automatically generated message.\n"
                + "\n"
                + "Below you will find the correct username and password.\n"
                + "\n"
                + "Your username : "+user.getIdUsuario()+"\n"
                + "\n"
                + "Your password : "+user.getPassword()+"\n"
                + "\n"
                + "Best regards, Support Team";
        initProps();
    }
    
    public void initProps(){
        props.put("mail.user", senderEmailID);
        props.put("mail.password", senderPassword);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", emailSMTPserver);
        props.put("mail.smtp.port", emailServerPort);
        props.put("mail.smtp.socketFactory.port", emailServerPort);
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.connectiontimeout", "7000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtps.ssl.checkserveridentity", "false");
        props.put("mail.smtps.ssl.trust", "*");
    }

    public static class SMTPAuthenticator extends javax.mail.Authenticator {
        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(senderEmailID, senderPassword);
        }
    }
    
    public static void crearEmail(Usuarios user) {
        (new Thread(new Email(user))).start();
    }
    
    public static void crearEmail(String Nombre,String correo,String Descripcion,String ciudad) {
        (new Thread(new Email(Nombre,correo,Descripcion,ciudad))).start();
    }

    public static void crearEmail(Notificaciones notification,String rutaArchivo, Object object, String tipoEvento){
        (new Thread(new Email(notification, rutaArchivo, object, tipoEvento))).start();
    }
    
    @Override
    public void run() {
        sendEmail();
    }
    
    public void sendEmail(){
        //Construye mail 
        try {
            Authenticator auth = new SMTPAuthenticator();
            Session session = Session.getInstance(props, auth);

            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderEmailID));
            
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(receiverEmails));
            if (ccEmails != null && !ccEmails.isEmpty()){
                msg.setRecipients(Message.RecipientType.CC,
                        InternetAddress.parse(ccEmails));
            }
            
            msg.setSubject(emailSubject);
            
            BodyPart cuerpoMensaje = new MimeBodyPart();
            cuerpoMensaje.setText(emailBody);
            //msg.setText(emailBody);
            Multipart multiparte = new MimeMultipart();
            multiparte.addBodyPart(cuerpoMensaje);

            //String nombreArchivo = "C:/Users/RHA/Desktop/Kmilo/NetBeansProyects/Email/src/email/consulta.pdf";
            if(rutaArchivo!=null){
                cuerpoMensaje = new MimeBodyPart();
                DataSource fuente = new FileDataSource(rutaArchivo);
                cuerpoMensaje.setDataHandler(new DataHandler(fuente));
                cuerpoMensaje.setFileName(rutaArchivo);
                multiparte.addBodyPart(cuerpoMensaje);
            }
            msg.setContent(multiparte);
            
            Transport tr = session.getTransport("smtps");
            tr.connect(emailSMTPserver, 465, senderEmailID, senderPassword);
            msg.saveChanges();
            
            tr.sendMessage(msg, msg.getAllRecipients());
            tr.close();
            //Transport.send(msg);
            System.out.println("Email: Mensaje Enviado");
        } catch (Exception mex) {
            System.out.println("Email: mal"+mex);
        }
    }
}
