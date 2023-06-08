package ro.axon.dot.service;

public abstract class MailService {
  public void sendNotification() {
    formatMessage();
    send();
  }

  protected abstract void formatMessage();
  protected abstract void send();
}
