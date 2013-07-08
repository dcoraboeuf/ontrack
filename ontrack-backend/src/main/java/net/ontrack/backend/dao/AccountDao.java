package net.ontrack.backend.dao;

import net.ontrack.backend.dao.model.TAccount;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.ID;

import java.util.List;

public interface AccountDao {

    TAccount findByNameAndPassword(String name, String password);

    String getRoleByModeAndName(String mode, String name);

    TAccount findByModeAndName(String mode, String name);

    TAccount getByID(int id);

    List<TAccount> findAll();

    ID createAccount(String name, String fullName, String email, String roleName, String mode, String password);

    void deleteAccount(int id);

    void updateAccount(int id, String name, String fullName, String email, String roleName);

    Ack changePassword(int id, String oldPassword, String newPassword);

    Ack changeEmail(int id, String password, String email);
}
