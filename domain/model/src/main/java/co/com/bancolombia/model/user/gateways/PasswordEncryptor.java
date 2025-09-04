package co.com.bancolombia.model.user.gateways;

public interface PasswordEncryptor {
    String encode(String password);
}