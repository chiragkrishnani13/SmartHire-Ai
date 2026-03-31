package com.cs.SmartHireAi.service;


import com.cs.SmartHireAi.Exceptions.DateOfBirthDoesNotExist;
import com.cs.SmartHireAi.Exceptions.RoleNotSpecefied;
import com.cs.SmartHireAi.Exceptions.UserDoesNotExist;
import com.cs.SmartHireAi.model.User;
import com.cs.SmartHireAi.model.UserProfileDetails;
import com.cs.SmartHireAi.model.UserResume;
import com.cs.SmartHireAi.reposistory.AuthReposistory;
import com.cs.SmartHireAi.reposistory.UserProfileDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
public class UserProfileDetailsService {
    @Autowired
    UserProfileDetailsRepository userProfileDetailsRepository;
    @Autowired
    AuthReposistory authReposistory;

    public void SaveUserDetails(UserProfileDetails userProfileDetails, Authentication authentication){
        User user=authReposistory.findByEmail(authentication.getName());
        userProfileDetails.setUser_id(user.getUser_id());
        if(userProfileDetails.getUser_id()==0){
            throw new UserDoesNotExist();
        }
        else if(userProfileDetails.getDate_of_birth()==null){
            throw new DateOfBirthDoesNotExist();
        }
        else if(userProfileDetails.getPreferred_role()==null){
            throw new RoleNotSpecefied();
        }
        userProfileDetailsRepository.save(
                userProfileDetails.getUser_id(),
                userProfileDetails.getDate_of_birth(),
                userProfileDetails.getGender(),
                userProfileDetails.getAddress(),
                userProfileDetails.getCity(),
                userProfileDetails.getState(),
                userProfileDetails.getCountry(),
                userProfileDetails.getPincode(),
                userProfileDetails.getLinkedin_url(),
                userProfileDetails.getGithub_url(),
                userProfileDetails.getPreferred_role(),
                userProfileDetails.getPreferred_location()
        );





    }
    public void updateuserprofile(UserProfileDetails userProfileDetails, Authentication authentication) {

        User user = authReposistory.findByEmail(authentication.getName());
        userProfileDetails.setUser_id(user.getUser_id());

        if (user.getUser_id() == 0) {
            throw new UserDoesNotExist();
        }
        userProfileDetailsRepository.updateProfile(userProfileDetails);
    }
    public String extractText(MultipartFile file) throws IOException {
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }
    public String extractByRegex(String text, String regex) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group() : "";
    }
    public String extractSection(String text, List<String> sectionNames) {

        String lowerText = text.toLowerCase();
        int start = -1;

        for (String section : sectionNames) {
            start = lowerText.indexOf(section.toLowerCase());
            if (start != -1) break;
        }
        if (start == -1) return "";
        int end = text.length();
        List<String> nextSections = new ArrayList<>();
        nextSections.add("Education");
        nextSections.add("Experience");
        nextSections.add("Project");
        nextSections.add("Skills");
        nextSections.add("Achievement");
        nextSections.add("summary");
        nextSections.add("Tools");
        nextSections.add("Frameworks");

        for (String next : nextSections) {
            int nextIndex = lowerText.indexOf(next, start + 10);
            if (nextIndex != -1 && nextIndex < end) {
                end = nextIndex;
            }
        }

        return text.substring(start, end).trim();
    }
    public Map<String, String> parseResume(String text) {
        Map<String, String> data = new HashMap<>();

        data.put("email", extractByRegex(text, "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"));
        data.put("phone", extractByRegex(text, "\\b\\d{10}\\b"));
        data.put("location", extractByRegex(text, "(?i)(Mumbai|Delhi|Pune|Bangalore|Hyderabad|Chennai|Kolkata)"));
        String linkedin=extractByRegex(text, "https?://(www\\.)?linkedin\\.com/in/[a-zA-Z0-9_-]+");
        if(linkedin==null || linkedin.isEmpty()){
            linkedin="LinkedIn";
        }
        data.put("linkedin_url",linkedin);
        String github=extractByRegex(text, "https?://(www\\.)?github\\.com/[a-zA-Z0-9_-]+");
        if(github==null || github.isEmpty()){
            github="Github";
        }
        data.put("github_url",github);

        data.put("profile_summary", extractSection(text,new ArrayList<>(List.of("summary", "profile summary"))));

        List<String> skillsList = new ArrayList<>();
        skillsList.add("Skills");
        skillsList.add("Technical Skills");
        skillsList.add("Key Skills");
        skillsList.add("Core Skills");
        skillsList.add("Professional Skills");
        skillsList.add("Technical Expertise");
        skillsList.add("Competencies");
        skillsList.add("Skill set");
        String skillsdata=extractSection(text, skillsList);
        if(skillsdata==null || skillsdata.trim().isEmpty()){
            throw  new RuntimeException("skills ka heading nhi hai bro");
        }

        data.put("Skills",skillsdata);



        List<String> toolsList = new ArrayList<>();
        toolsList.add("Tool");
        toolsList.add("Tools");
        toolsList.add("Development Tools");
        toolsList.add("Software Tools");
        toolsList.add("Tools & Technologies");
        toolsList.add("Tools and Technologies");
        toolsList.add("Technologies");
        toolsList.add("Platforms");
        toolsList.add("Tools & Platforms");
        toolsList.add("Dev Tools");
        toolsList.add("Software");
        toolsList.add("Technologies & Tools");
        String toolskadata=extractSection(text, toolsList);
        if(toolskadata==null || toolskadata.trim().isEmpty()){
            throw  new RuntimeException("tools ka heading nhi hai bro");
        }

        data.put("Tools",toolskadata);





        List<String> frameworkList = new ArrayList<>();
        frameworkList.add("Frameworks");
        frameworkList.add("Libraries");
        frameworkList.add("Frameworks & Libraries");
        frameworkList.add("Web Frameworks");
        frameworkList.add("Backend Frameworks");
        frameworkList.add("Frontend Frameworks");
        frameworkList.add("Technologies");
        frameworkList.add("Tech stack");
        frameworkList.add("Development Frameworks");
        frameworkList.add("Software Frameworks");
        String frameworkkadata=extractSection(text, frameworkList);
        if(frameworkkadata==null || frameworkkadata.trim().isEmpty()){
            throw  new RuntimeException("frameworks ka heading nhi hai bro");
        }

        data.put("Frameworks",frameworkkadata);


        List<String> experienceList = new ArrayList<>();
        experienceList.add("Career Experience");
        experienceList.add("Experience");
        experienceList.add("Work Experience");
        experienceList.add("Professional Experience");
        experienceList.add("Career");
        experienceList.add("Career History");
        experienceList.add("Employment History");
        experienceList.add("Work History");
        experienceList.add("Job Experience");
        experienceList.add("Internship");
        experienceList.add("Internships");
        experienceList.add("Industrial Experience");
        experienceList.add("Relevant experience");
        String experiencekidata=extractSection(text, experienceList);
        if(experiencekidata==null || experiencekidata.trim().isEmpty()){
            throw  new RuntimeException("experience ka heading nhi hai bro");
        }

        data.put("Experience",experiencekidata);


        List<String> educationList = new ArrayList<>();

        educationList.add("Education");
        educationList.add("Academic Background");
        educationList.add("Academic Qualifications");
        educationList.add("Educational Qualifications");
        educationList.add("Academic Details");
        educationList.add("Qualification");
        educationList.add("Qualifications");
        educationList.add("Education details");
        educationList.add("Academic history");
        educationList.add("Educational background");
        educationList.add("Degree");
        educationList.add("Academics");
        String educationkidata=extractSection(text, educationList);
        if(educationkidata==null || educationkidata.trim().isEmpty()){
            throw  new RuntimeException("Education ka heading nhi hai bro");
        }
        data.put("Education",educationkidata);


        List<String> projectList = new ArrayList<>();
        projectList.add("Projects");
        projectList.add("Project");
        projectList.add("Academic Projects");
        projectList.add("Personal Projects");
        projectList.add("Key Projects");
        projectList.add("Major Projects");
        projectList.add("Minor Projects");
        projectList.add("Project Experience");
        projectList.add("Project Work");
        projectList.add("Projects & Work");
        projectList.add("Relevant Projects");
        projectList.add("Academic Work");
        projectList.add("Practical Projects");
        String projectkidata=extractSection(text, projectList);
        if(projectkidata==null || projectkidata.trim().isEmpty()){
            throw  new RuntimeException("Project ka heading nhi hai bro");
        }
        data.put("Project",projectkidata);


        List<String> achievementList = new ArrayList<>();
        achievementList.add("Achievements");
        achievementList.add("Certificate");
        achievementList.add("Awards");
        achievementList.add("Awards & Scholarships");
        achievementList.add("Honors");
        achievementList.add("Honors & Awards");
        achievementList.add("Accomplishments");
        achievementList.add("Key Achievements");
        achievementList.add("Academic Achievements");
        achievementList.add("Scholarships");
        achievementList.add("Recognitions");
        achievementList.add("Certifications");
        achievementList.add("Achievements & Awards");
        achievementList.add("Awards and Achievements");
        String achievementskidata=extractSection(text, achievementList);

        if(achievementskidata==null || achievementskidata.trim().isEmpty()){
            throw  new RuntimeException("achievement ka heading nhi hai bro");
        }
        data.put("Achievement",achievementskidata);

        return data;
    }
    public void saveresume(MultipartFile multipartFile,UserResume userResume,Authentication authentication)throws IOException {
        System.out.println(multipartFile.getOriginalFilename());
        User user = authReposistory.findByEmail(authentication.getName());
        userResume.setUser_id(user.getUser_id());
        if (user.getUser_id() == 0) {
            throw new UserDoesNotExist();
        }
        List<String> errors = new ArrayList<>();
        if (multipartFile.isEmpty()) {
            errors.add("File is empty");
        }
        if (!"application/pdf".equals(multipartFile.getContentType())) {
            errors.add("ONLY PDF ALLOWED");
        }
        if (!errors.isEmpty()) {
            throw new RuntimeException(errors.toString());
        }
        String uploadDir = "uploads/";
        String fileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
        Path filePath = Paths.get(uploadDir + fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, multipartFile.getBytes());
        String fileUrl = filePath.toString();
        String text = extractText(multipartFile);
        Map<String, String> parseddata = parseResume(text);
        System.out.println(parseddata);
        userProfileDetailsRepository.saveResume(
                user.getUser_id(),
                user.getFull_name(),
                parseddata.get("email"),
                parseddata.get("phone"),
                parseddata.get("location"),
                parseddata.get("linkedin_url"),
                parseddata.get("github_url"),
                parseddata.get("profile_summary"),
                parseddata.get("Skills"),
                parseddata.get("Tools"),
                parseddata.get("Frameworks"),
                parseddata.get("Experience"),
                parseddata.get("Education"),
                parseddata.get("Project"),
                parseddata.get("Achievement"),
                multipartFile.getOriginalFilename(),
                multipartFile.getContentType(),
                1);




    }




}
