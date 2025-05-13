package myParking_Backend.Backend.Users;
import jakarta.transaction.Transactional;
import myParking_Backend.Backend.Bookings.Booking;
import myParking_Backend.Backend.Bookings.Service_Booking;
import myParking_Backend.Backend.EmailService.EmailService;
import myParking_Backend.Backend.JWT.JwtUtil;
import myParking_Backend.Backend.Parking.Parking;
import myParking_Backend.Backend.Parking.Service_Parking;
import myParking_Backend.Backend.Users.ResponceEntities.ResponceMyProfileCustomer;
import myParking_Backend.Backend.Users.ResponceEntities.ResponceMyProfileOwner;
import myParking_Backend.Backend.Users.ResponceEntities.ResponseCustomers;
import myParking_Backend.Backend.Users.ResponceEntities.ResponseOwners;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:4200")
public class Controller_User {


    private final AuthenticationManager authenticationManager;
    private final Service_Users service;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final Service_Parking service_parking;
    private final Service_Booking service_Booking;
    private EmailService emailService;

    @Autowired
    public Controller_User(JwtUtil jwtUtil, AuthenticationManager authenticationManager, Service_Users service_test, PasswordEncoder passwordEncoder, Service_Parking serviceParking, Service_Booking serviceBooking, EmailService emailService ) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.service = service_test;
        this.passwordEncoder = passwordEncoder;
        this.service_parking = serviceParking;
        this.service_Booking = serviceBooking;
        this.emailService = emailService;
    }



    @PostMapping("/register_customer")
    public ResponseEntity<?> register(@RequestBody  Users user){
        System.out.println(user.toString());
        user.setCustomer(true);
        user.setOwner(false);
        user.setAfm_Owner("none");
        user.setIdentity("none");
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_CUSTOMER");
        service.saveuser(user, roles);
        String email = user.getEmail();
        String subject = "Επιτυχής Εγγραφή στο myParking!";
        String body = user.getUsername() + "καλωσήρθες στο myParking! Σε ευχαριστούμε για την εγγραφή. Ήρθε η ώρα για την πρώτη σου κράτηση. Μπες στην εφαρμογή: " +
                "http:localhost:4200 " + "και ξεκίνα!";
        this.emailService.sendSimpleEmail(email, subject, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Καταχωρήθηκε"));
    }

    @PostMapping("/register_owner")
    public ResponseEntity<?> registerOwner(@RequestBody  Users user){
        user.setCustomer(false);
        user.setOwner(true);
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_OWNER");
        service.saveuser(user, roles);
        String email = user.getEmail();
        String subject = "Επιτυχής Εγγραφή στο myParking!";
        String body = user.getUsername() + "καλωσήρθες στο myParking! Σε ευχαριστούμε για την εγγραφή. Μπες στην εφαρμογή: " +
                "http:localhost:4200/login " + "και ξεκίνα να διαχειρίζεσαι το pakring σου!";
        this.emailService.sendSimpleEmail(email, subject, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Καταχωρήθηκε"));
    }

//$2a$10$skj9f7qYBEr3PtGeRE7rNOH6I2T6uPdbz03yF07yEtv/YjmG5X9zW


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login request) {
        System.out.println(request.getPassword());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Users user = (Users) authentication.getPrincipal();
        Set<String> roles = user.getRoles();
        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity.ok(Map.of("token", token, "roles", roles));
    }



    //manage data from frontend
    @Transactional
    @GetMapping("/myprofile")
    public ResponseEntity<?> performLogout(Authentication authentication) {


        Users user = (Users) authentication.getPrincipal();//ΝΑ ΤΟ ΜΕΛΕΤΗΣΩ
        if (user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_CUSTOMER"))){
            ResponceMyProfileCustomer resp = new ResponceMyProfileCustomer();
            resp.setId(user.getId());
            resp.setUsername(user.getUsername());
            resp.setEmail(user.getEmail());
            resp.setLastname(user.getLastname());
            resp.setName(user.getName());
            return ResponseEntity.ok(resp);
        }
        else if (user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_OWNER"))) {
            ResponceMyProfileOwner resp = new ResponceMyProfileOwner();
            resp.setId(user.getId());
            resp.setUsername(user.getUsername());
            resp.setEmail(user.getEmail());
            resp.setAfm_owner(user.getAfm_Owner());
            resp.setLastname(user.getLastname());
            resp.setName(user.getName());
            return ResponseEntity.ok(resp);
        }

        else if (user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            ResponceMyProfileOwner resp = new ResponceMyProfileOwner();
            resp.setId(user.getId());
            resp.setUsername(user.getUsername());
            resp.setEmail(user.getEmail());
            resp.setAfm_owner(user.getAfm_Owner());
            resp.setLastname(user.getLastname());
            resp.setName(user.getName());
            return ResponseEntity.ok(resp);
        }
        else{
            return ResponseEntity.ok(user);
        }


    }


    @PostMapping("/updatemyprofile/username")
    public ResponseEntity<?> updatemyprofileusername(@RequestBody Map<String, String> request, Authentication authentication) {
        Users user = (Users) authentication.getPrincipal();
        user.setUsername(request.get("username"));
        service.updateUser(user);
        return ResponseEntity.ok(Map.of("message","Ολοκληρώθηκε με επιτυχία"));
    }

    @PostMapping("/updatemyprofile/email")
    public ResponseEntity<?> updatemyprofileemail(@RequestBody Map<String, String> request, Authentication authentication) {

        Users user = (Users) authentication.getPrincipal();
        user.setEmail(request.get("email"));
        service.updateUser(user);
        return ResponseEntity.ok(Map.of("message","Ολοκληρώθηκε με επιτυχία"));
    }

    @PostMapping("/updatemyprofile/name")
    public ResponseEntity<?> updatemyprofilename(@RequestBody Map<String, String> request, Authentication authentication) {

        Users user = (Users) authentication.getPrincipal();
        user.setName(request.get("name"));
        service.updateUser(user);
        return ResponseEntity.ok(Map.of("message","Ολοκληρώθηκε με επιτυχία"));
    }

    @PostMapping("/updatemyprofile/lastname")
    public ResponseEntity<?> updatemyprofilelastname(@RequestBody Map<String, String> request, Authentication authentication) {

        Users user = (Users) authentication.getPrincipal();
        user.setLastname(request.get("lastname"));
        service.updateUser(user);
        return ResponseEntity.ok(Map.of("message","Ολοκληρώθηκε με επιτυχία"));
    }

    @PostMapping("/updatemyprofile/changecredentials")
    public ResponseEntity<?> updatemyprofilechangecredentials(@RequestBody Map<String, String> request, Authentication authentication ){
        Users user = (Users) authentication.getPrincipal();
        if(!passwordEncoder.matches(request.get("oldPassword"),user.getPassword()))//ΤΣΕΚΑΡΕΙ ΑΝ Ο ΑΠΟΚΡΥΠΤΟΓΡΑΦΗΜΕΝΟΣ ΝΕΟΣ ΚΩΔΙΚΟΣ ΕΙΝΑΙ ΙΔΙΟΣ ΜΕ ΤΟΝ ΚΡΥΤΟΓΡΑΦΗΜΕΝΟ ΗΔΗ ΥΠΑΡΨΒ ΚΩΔΙΚΟ
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Λάθος Κωδικός!");
        else{
            if(!request.get("newPassword").equals(request.get("newPasswordValidate")))
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Νέος Κωδικός δεν ταιριάζει με validate!");
            else{
                user.setPassword(request.get("newPassword"));
                service.updateUserPassword(user);
                return ResponseEntity.ok(Map.of("message","Ολοκληρώθηκε με επιτυχία"));
            }


        }

    }


    @GetMapping("/getCustomers")
    public ResponseEntity<?> getCustomers(){
        List<Users> customers = service.getCustomers();
        List<ResponseCustomers> responseCustomers = new ArrayList<>();
        for (Users customer:customers){
            ResponseCustomers responce = new ResponseCustomers();
            responce.setId(customer.getId());
            responce.setUsername(customer.getUsername());
            responce.setName(customer.getName());
            responce.setLastname(customer.getLastname());
            responce.setEmail(customer.getEmail());
            List<Booking> bookings = service_Booking.findByUsers_of_booking(customer);
            responce.setTotalBookings(bookings.size());
            double cost=0;
            for (Booking booking:bookings){
                cost+=booking.getCost();
            }
            responce.setTotalcost(cost);
            responseCustomers.add(responce);
        }


        return ResponseEntity.ok(responseCustomers);
    }


    @GetMapping("/getOwners")
    public ResponseEntity<?> getOwners(){
        List<Users> owners = service.getOwners();
        List<ResponseOwners> responseOwners = new ArrayList<>();

        for (Users owner:owners){
            Set<Parking> parkings = service_parking.getParkingByUsers(owner);
            List<String> nameofparkings = new ArrayList<>();
            List<Long> idparkings = new ArrayList<>();
            for (Parking parking:parkings){
                nameofparkings.add(parking.getName_of_parking());
                idparkings.add(parking.getId());
            }
            ResponseOwners responce = new ResponseOwners();
            responce.setId(owner.getId());
            responce.setUsername(owner.getUsername());
            responce.setName(owner.getName());
            responce.setLastname(owner.getLastname());
            responce.setEmail(owner.getEmail());
            responce.setAfmOwner(owner.getAfm_Owner());
            responce.setIdentityOwner(owner.getIdentity());
            responce.setNamesOfParking(nameofparkings);
            responce.setIdParkings(idparkings);
            responseOwners.add(responce);
        }


        return ResponseEntity.ok(responseOwners);
    }



}

