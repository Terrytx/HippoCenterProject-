package tw.com.hippo_center_backend.h3controller.b1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import tw.com.hippo_center_backend.h5util.b5.EcpayFunctions;

@Controller
@RequestMapping("/pages/ecpay")
public class EcpayController {
	@Autowired
	private EcpayFunctions ecpayFunctions;
	
	@PostMapping("/return")
	public String ecpayReturn(@RequestBody String body) {
		System.out.println("ecpay return "+System.currentTimeMillis());
		System.out.println("body="+body);
		
		return "";
	}
	
	@PostMapping("/send")
	@ResponseBody
	@CrossOrigin
	public String send(@RequestBody String body) {
		String form = ecpayFunctions.buildEcpayForm(body);
		
        return form;
	}
}
