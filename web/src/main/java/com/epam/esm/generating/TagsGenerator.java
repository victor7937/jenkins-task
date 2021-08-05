package com.epam.esm.generating;

import com.epam.esm.criteria.UserCriteria;
import com.epam.esm.dto.OrderDTO;
import com.epam.esm.dto.PagedDTO;
import com.epam.esm.dto.UserDTO;
import com.epam.esm.entity.User;
import com.epam.esm.service.OrderService;
import com.epam.esm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.*;
import java.util.stream.Stream;

@Component
public class TagsGenerator {


//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private OrderService orderService;

    public void generateTags() {


    }

//    public void generateOrders(){
//        Random random = new Random();
//        for (int i = 0; i < 50; i++){
//            PagedDTO<User> users = userService.get(UserCriteria.createCriteria(new HashMap<>()),50, random.nextInt(20) + 1);
//            for (User user : users.getPage()){
//                orderService.makeOrder(new OrderDTO(user.getEmail() ,(long)random.nextInt(10000) + 1L));
//                // OrderDTO dto = new OrderDTO(user.getEmail() ,(long)random.nextInt(10000) + 1L);
//                //System.out.println(dto);
//            }
//        }
//    }


//        String names = readFile("name_words.txt");
//        String descriptions = readFile("descr_words.txt");
//
//        BreakIterator boundary = BreakIterator.getWordInstance(Locale.US);
//        boundary.setText(names);
//        List<String> namesList = splitByFragments(boundary,names,"[a-zA-Z-]+");
//        Collections.shuffle(namesList);
//
//        boundary = BreakIterator.getWordInstance(Locale.US);
//        boundary.setText(descriptions);
//        List<String> descrList = splitByFragments(boundary, descriptions,"[a-zA-Z-]+");
//        Collections.shuffle(namesList);

//        Random random = new Random();
//        try {
//            for (int i = 0; i < 20; i++){
//                PagedDTO<User> users = userRepository.getByCriteria(UserCriteria.createCriteria(new HashMap<>()),50, random.nextInt(20) + 1);
//                for (User user : users.getPage()){
//                    orderService.makeOrder(new OrderDTO(user.getEmail() ,(long)random.nextInt(10000) + 1L));
////                OrderDTO dto = new OrderDTO(user.getEmail() ,(long)random.nextInt(1000) + 1L);
////                System.out.println(dto);
//                }
//            }
//
//
//
//        } catch (RepositoryException | ServiceException e) {
//                e.printStackTrace();
//        }
//
//    }

//    private static String generateCertName(List<String> names){
//        StringBuilder name = new StringBuilder();
//        Random random = new Random();
//        name.append(names.get(random.nextInt(names.size())));
//
//        for (int i = 0; i < random.nextInt(3) + 2; i++){
//            name.append(" ").append(names.get(random.nextInt(names.size())));
//        }
//        return name.toString();
//    }
//
//    private static String generateDescrName(List<String> descriptions){
//        StringBuilder descr = new StringBuilder();
//        Random random = new Random();
//        descr.append(descriptions.get(random.nextInt(descriptions.size())));
//
//        for (int i = 0; i < random.nextInt(15) + 5; i++){
//            descr.append(" ").append(descriptions.get(random.nextInt(descriptions.size())));
//        }
//        return descr.toString();
//    }
//
    public static List<String> splitByFragments(BreakIterator boundary, String source, String fragmentPattern) {
        int start = boundary.first();
        List<String> fragmentsList = new ArrayList<>();
        for (int end = boundary.next();
             end != BreakIterator.DONE;
             start = end, end = boundary.next()) {
            String fragment = source.substring(start,end);
            if (fragment.matches(fragmentPattern)){
                fragmentsList.add(fragment);
            }
        }
        return fragmentsList;
    }

    public static String readFile (String fileName) {
        boolean fileErrFlag = false;
        StringBuilder fileStringFormatBuilder = new StringBuilder();
        Stream<String> lines = null;
        try {
           // URL url = ClassLoader.getSystemResource(fileName);
            //if (url != null) {
                lines = Files.lines(Paths.get(fileName));
                lines.forEach(line -> fileStringFormatBuilder.append(line.strip()).append(" "));
            //}
        } catch (IOException e) {
            fileErrFlag = true;
        } finally {
            if (lines != null) {
                lines.close();
            }
        }
        return fileErrFlag ? "" : fileStringFormatBuilder.toString();
    }

//    void generateUsers(){
//        String names = readFile("/home/victor/Documents/EPAM/Lab/TaskRepo/RestApiApp/web/src/main/resources/last-names.txt");
//
//        String lastNames = readFile("/home/victor/Documents/EPAM/Lab/TaskRepo/RestApiApp/web/src/main/resources/names.txt");
//
//
//        BreakIterator boundary = BreakIterator.getWordInstance(Locale.US);
//        boundary.setText(names);
//        List<String> namesList = splitByFragments(boundary,names,"[a-zA-Z-]+");
//        Collections.shuffle(namesList);
//
//        boundary = BreakIterator.getWordInstance(Locale.US);
//        boundary.setText(lastNames);
//        List<String> lastNamesList = splitByFragments(boundary, lastNames,"[a-zA-Z-]+");
//        Collections.shuffle(lastNamesList);
//
//        for (int i = 0; i < 1000; i++){
//            String name = namesList.get(i).charAt(0) + namesList.get(i).toLowerCase().substring(1);
//            String suranme = lastNamesList.get(i);
//            String email = name.toLowerCase() + "_" + suranme.toLowerCase() + "@gmail.com";
//            UserDTO userDTO = new UserDTO(email, name.toLowerCase(), name, suranme);
//            userService.registration(userDTO);
//        }
//
//    }
}
//}

//for (int i = 1; i < 2787; i++) {
//
//                Set<Tag> tags = new HashSet<>();
//                for (int j = 0; j < random.nextInt(5) + 2; j++) {
//                    tags.add(tagRepository.getById((long) random.nextInt(1000) + 1L));
//                }
//                CertificateDTO certificateDTO = new CertificateDTO(generateCertName(namesList), generateDescrName(descrList),
//                        random.nextFloat() * 50.0f , 1 + random.nextInt(60), tags);
//                certificateRepository.add(certificateDTO);
//            }
