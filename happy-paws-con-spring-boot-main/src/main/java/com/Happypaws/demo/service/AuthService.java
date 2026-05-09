package com.Happypaws.demo.service;
import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repository;

    public AuthService(UserRepository repository) {
        this.repository = repository;
    }

    public boolean login(String email, String password) {

        User user = repository.findByEmail(email)
                .orElse(null);

        if(user == null){
            return false;
        }

        return user.getPassword().equals(password);
    }

    public void saveUser(User user) {
        repository.save(user);
    }
}