package com.mineguard.platform.shared.infrastructure.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from:noreply@mineguard.com}")
    private String fromAddress;

    private static final Map<String, String> ROLE_LABELS = Map.of(
            "ROLE_ADMINISTRATOR", "Administrador",
            "ROLE_ADMIN",         "Administrador",
            "ROLE_SUPERVISOR",    "Supervisor",
            "ROLE_DRIVER",        "Conductor"
    );

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    @Async
    @Override
    public void sendCredentialsEmail(String toEmail, String role, String generatedUsername,
                                     String recipientName, String temporaryPassword) {
        send(toEmail,
             "MineGuard — Credenciales de acceso a la plataforma",
             buildCredentialsBody(toEmail, role, generatedUsername, recipientName, temporaryPassword));
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String toEmail, String temporaryPassword) {
        send(toEmail,
             "MineGuard — Restablecimiento de contrasena",
             buildPasswordResetBody(toEmail, temporaryPassword));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Internal helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void send(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, "MineGuard Platform");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("[EmailService] Correo enviado a {}", toEmail);
        } catch (Exception ex) {
            log.error("[EmailService] Error al enviar correo a {}: {}", toEmail, ex.getMessage(), ex);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HTML templates — Light Corporate Theme
    // Palette: background #F8FAFC | card #FFFFFF | accent #FCB502
    //          text #0F172A | subtext #475569 | border #E2E8F0
    // ─────────────────────────────────────────────────────────────────────────

    private String buildCredentialsBody(String toEmail, String role, String generatedUsername,
                                        String recipientName, String temporaryPassword) {
        var roleLabel = ROLE_LABELS.getOrDefault(role, role);
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
                  <title>Credenciales MineGuard</title>
                </head>
                <body style="margin:0;padding:0;background-color:#F8FAFC;
                             font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0"
                         style="background:#F8FAFC;padding:48px 16px;">
                    <tr><td align="center">

                      <table width="560" cellpadding="0" cellspacing="0"
                             style="background:#FFFFFF;border:1px solid #E2E8F0;
                                    border-radius:8px;box-shadow:0 1px 4px rgba(0,0,0,.06);">

                        <!-- Top accent bar -->
                        <tr>
                          <td style="background:#FCB502;height:4px;border-radius:8px 8px 0 0;
                                     font-size:0;line-height:0;">&nbsp;</td>
                        </tr>

                        <!-- Header -->
                        <tr>
                          <td style="padding:32px 40px 24px;">
                            <p style="margin:0;font-size:13px;font-weight:600;letter-spacing:.08em;
                                      color:#FCB502;text-transform:uppercase;">MineGuard Platform</p>
                            <h1 style="margin:8px 0 0;font-size:22px;font-weight:700;
                                       color:#0F172A;line-height:1.3;">
                              Tus credenciales de acceso
                            </h1>
                          </td>
                        </tr>

                        <!-- Greeting -->
                        <tr>
                          <td style="padding:0 40px 24px;">
                            <p style="margin:0;font-size:15px;color:#475569;line-height:1.6;">
                              Hola <strong style="color:#0F172A;">%s</strong>,<br/>
                              tu cuenta ha sido creada en la plataforma MineGuard.
                              A continuacion encontraras los datos necesarios para iniciar sesion.
                            </p>
                          </td>
                        </tr>

                        <!-- Credentials card -->
                        <tr>
                          <td style="padding:0 40px 32px;">
                            <table width="100%%" cellpadding="0" cellspacing="0"
                                   style="background:#F8FAFC;border:1px solid #E2E8F0;
                                          border-radius:6px;">
                              <tr>
                                <td style="padding:24px;">
                                  %s
                                  %s
                                  %s
                                  %s
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>

                        <!-- Warning banner -->
                        <tr>
                          <td style="padding:0 40px 32px;">
                            <table width="100%%" cellpadding="0" cellspacing="0"
                                   style="background:#FFFBEB;border:1px solid #FCD34D;border-radius:6px;">
                              <tr>
                                <td style="padding:14px 18px;">
                                  <p style="margin:0;font-size:13px;color:#92400E;line-height:1.5;">
                                    <strong>Importante:</strong> Esta contrasena es temporal.
                                    El sistema te pedira crear una nueva contrasena definitiva al iniciar sesion.
                                  </p>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>

                        <!-- Footer -->
                        <tr>
                          <td style="padding:20px 40px;border-top:1px solid #E2E8F0;">
                            <p style="margin:0;font-size:12px;color:#94A3B8;">
                              MineGuard Platform &middot; Plataforma de Seguridad en Operaciones Mineras<br/>
                              Si no esperabas este correo, puedes ignorarlo de forma segura.
                            </p>
                          </td>
                        </tr>

                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(
                recipientName,
                credentialRow("Empresa / Cuenta", recipientName),
                credentialRow("Rol asignado", roleLabel),
                credentialRow("Identificador de usuario", generatedUsername),
                credentialRowHighlight("Contrasena temporal", temporaryPassword)
        );
    }

    private String buildPasswordResetBody(String toEmail, String temporaryPassword) {
        return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
                  <title>Restablecer contrasena MineGuard</title>
                </head>
                <body style="margin:0;padding:0;background-color:#F8FAFC;
                             font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0"
                         style="background:#F8FAFC;padding:48px 16px;">
                    <tr><td align="center">

                      <table width="560" cellpadding="0" cellspacing="0"
                             style="background:#FFFFFF;border:1px solid #E2E8F0;
                                    border-radius:8px;box-shadow:0 1px 4px rgba(0,0,0,.06);">

                        <!-- Top accent bar -->
                        <tr>
                          <td style="background:#FCB502;height:4px;border-radius:8px 8px 0 0;
                                     font-size:0;line-height:0;">&nbsp;</td>
                        </tr>

                        <!-- Header -->
                        <tr>
                          <td style="padding:32px 40px 24px;">
                            <p style="margin:0;font-size:13px;font-weight:600;letter-spacing:.08em;
                                      color:#FCB502;text-transform:uppercase;">MineGuard Platform</p>
                            <h1 style="margin:8px 0 0;font-size:22px;font-weight:700;
                                       color:#0F172A;line-height:1.3;">
                              Restablecimiento de contrasena
                            </h1>
                          </td>
                        </tr>

                        <!-- Body -->
                        <tr>
                          <td style="padding:0 40px 24px;">
                            <p style="margin:0;font-size:15px;color:#475569;line-height:1.6;">
                              Recibimos una solicitud para restablecer la contrasena asociada
                              a la cuenta <strong style="color:#0F172A;">%s</strong>.
                              Tu nueva contrasena temporal es:
                            </p>
                          </td>
                        </tr>

                        <!-- Password card -->
                        <tr>
                          <td style="padding:0 40px 32px;">
                            <table width="100%%" cellpadding="0" cellspacing="0"
                                   style="background:#F8FAFC;border:1px solid #E2E8F0;border-radius:6px;">
                              <tr>
                                <td style="padding:24px;">
                                  %s
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>

                        <!-- Info banner -->
                        <tr>
                          <td style="padding:0 40px 32px;">
                            <table width="100%%" cellpadding="0" cellspacing="0"
                                   style="background:#FFFBEB;border:1px solid #FCD34D;border-radius:6px;">
                              <tr>
                                <td style="padding:14px 18px;">
                                  <p style="margin:0;font-size:13px;color:#92400E;line-height:1.5;">
                                    <strong>Por seguridad</strong>, el sistema te pedira crear una nueva
                                    contrasena definitiva al iniciar sesion con estas credenciales.
                                  </p>
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>

                        <!-- Footer -->
                        <tr>
                          <td style="padding:20px 40px;border-top:1px solid #E2E8F0;">
                            <p style="margin:0;font-size:12px;color:#94A3B8;">
                              MineGuard Platform &middot; Plataforma de Seguridad en Operaciones Mineras<br/>
                              Si no solicitaste este restablecimiento, ignora este correo.
                            </p>
                          </td>
                        </tr>

                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(toEmail, credentialRowHighlight("Contrasena temporal", temporaryPassword));
    }

    /** Renders a label/value row inside a credentials card. */
    private static String credentialRow(String label, String value) {
        return """
               <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:12px;">
                 <tr>
                   <td style="font-size:11px;font-weight:600;letter-spacing:.06em;color:#94A3B8;
                              text-transform:uppercase;padding-bottom:2px;">%s</td>
                 </tr>
                 <tr>
                   <td style="font-size:15px;color:#0F172A;font-weight:500;">%s</td>
                 </tr>
               </table>
               """.formatted(label, value);
    }

    /** Same as {@link #credentialRow} but renders the value in a highlighted monospace code badge. */
    private static String credentialRowHighlight(String label, String value) {
        return """
               <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:4px;">
                 <tr>
                   <td style="font-size:11px;font-weight:600;letter-spacing:.06em;color:#94A3B8;
                              text-transform:uppercase;padding-bottom:6px;">%s</td>
                 </tr>
                 <tr>
                   <td>
                     <span style="display:inline-block;background:#0F172A;color:#FCB502;
                                  font-family:monospace;font-size:16px;letter-spacing:.08em;
                                  font-weight:700;padding:8px 16px;border-radius:4px;">%s</span>
                   </td>
                 </tr>
               </table>
               """.formatted(label, value);
    }
}
