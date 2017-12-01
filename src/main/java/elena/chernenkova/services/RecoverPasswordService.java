package elena.chernenkova.services;


import elena.chernenkova.common.GeneralSettings;
import elena.chernenkova.email.EmailSender;
import elena.chernenkova.entities.RecoverEntity;
import elena.chernenkova.entities.User;
import elena.chernenkova.repositories.RecoverRepository;
import elena.chernenkova.repositories.UserRepository;
import elena.chernenkova.wrappers.recoverpasswordwrappers.EmailWrapper;
import elena.chernenkova.wrappers.recoverpasswordwrappers.PasswordWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class RecoverPasswordService {

    private final String RECOVERING_MESSAGE =
            "Link to recover your password is bellow.\n" +
            "If You did not make this request, just ignore this message\n";
    private final String RECOVER_CONFIRM_REQUEST_HTTP = GeneralSettings.DOMAIN
            + GeneralSettings.RECOVER_PASSWORD
            + GeneralSettings.RECOVER_CONFIRM + "/";
    private final String RECOVERING_THEME = "recovering password";
    private final long TIME_TO_VALIDITY = 1000*3600*3;

    private UserRepository userRepository;
    private RecoverRepository recoverRepository;
    @Autowired
    public RecoverPasswordService(UserRepository userRepository, RecoverRepository recoverRepository) {
        this.userRepository = userRepository;
        this.recoverRepository = recoverRepository;
    }

    /**
     * finds user by username (email) and adds it into DB and send an email notification
     * @see RecoverEntity
     * @param wrapper contains email string
     * @return OK status
     */
    public ResponseEntity requestOnRecovering(EmailWrapper wrapper){
        User user = userRepository.findByUsername(wrapper.getEmail());
        if(user != null){
            //Such user exists
            Optional<RecoverEntity> current = recoverRepository.findByUsername(user.getUsername());
            RecoverEntity entity;
            //TODO: create more unique string
            String secretString = user.getUsername().hashCode() + "-" + UUID.randomUUID().toString();
            if(current.isPresent()){
                //This user has already requested on recovering password
                entity = current.get();
                entity.setDate(new Date());
                entity.setSecretString(secretString);
            }else{
                entity = new RecoverEntity(user.getUsername(), secretString);
            }
            entity = recoverRepository.saveAndFlush(entity);
            EmailSender sender = new EmailSender(entity.getUsername(), RECOVERING_THEME,
                    RECOVERING_MESSAGE + RECOVER_CONFIRM_REQUEST_HTTP + entity.getSecretString());
            sender.send();
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * checks the validity of request on recover using UUID and recovers password
     * @param wrapper contains password string
     * @param uuid - path variable in request. unique for every person
     * @return OK status
     */
    public ResponseEntity recoverPassword(PasswordWrapper wrapper, String uuid){
        Optional<RecoverEntity> entityOptional = recoverRepository.findBySecretString(uuid);
        if(entityOptional.isPresent()){
            RecoverEntity current = entityOptional.get();
            if(new Date().getTime() - current.getDate().getTime() < TIME_TO_VALIDITY){
                User user = userRepository.findByUsername(current.getUsername());
                if(user != null){
                    //TODO add MD5 hash function
                    user.setUserPassword(wrapper.getPassword());
                    user.setLastPasswordResetDate(new Date());
                    userRepository.saveAndFlush(user);
                    recoverRepository.delete(current);
                    return new ResponseEntity(HttpStatus.OK);

                }
            }
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}