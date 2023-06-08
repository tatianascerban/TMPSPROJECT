package ro.axon.dot.service;

public class VacationNotification extends MailService {

  @Override
  protected void formatMessage() {
    System.out.println("Formatting email message");
  }

  @Override
  protected void send() {
    System.out.println("Sending email vacation");
  }
}
