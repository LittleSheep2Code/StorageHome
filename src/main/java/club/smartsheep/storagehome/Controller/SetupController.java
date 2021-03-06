package club.smartsheep.storagehome.Controller;

import club.smartsheep.storagehome.DAO.Entity.ConfigEntity;
import club.smartsheep.storagehome.DAO.Entity.UserEntity;
import club.smartsheep.storagehome.DAO.Mappers.ConfigMapper;
import club.smartsheep.storagehome.Services.Accounts.AccountManagementService;
import club.smartsheep.storagehome.Services.CacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/setup")
public class SetupController {

    @Autowired
    CacheService cacheService;

    @Autowired
    AccountManagementService accountManagementService;

    @Autowired
    private ConfigMapper configMapper;

    @RequestMapping()
    public String Setup(Model model, @RequestParam(defaultValue = "0") Integer step, @RequestParam(required = false) String message) {
        if(configMapper.selectByName("setup.done") != null && configMapper.selectByName("setup.done").getValue().equalsIgnoreCase("TRUE")) {
            return "redirect:/";
        }

        model.addAttribute("admin", new UserEntity());
        model.addAttribute("step", step);
        model.addAttribute("lmessage_danger", message);
        return "setup";
    }

    @PostMapping("/set-administer")
    public String SetAdminister(RedirectAttributes redirectAttributes, UserEntity entity) {
        entity.setRole("administer");
        AccountManagementService.AddResponse response = accountManagementService.addNewUser(entity);
        if(response != AccountManagementService.AddResponse.Successful) {
            return "redirect:/setup?setup=1&message=The%20set%20administer%20step%20is%20failed";
        }

        return "redirect:/setup?step=2";
    }

    @GetMapping("/done")
    public String CleanUp() {
        ConfigEntity entity = new ConfigEntity();
        entity.setName("setup.done");
        entity.setValue("TRUE");
        configMapper.insert(entity);

        cacheService.ClearAll();

        return "redirect:/";
    }
}
