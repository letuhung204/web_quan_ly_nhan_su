package base.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import base.model.Employee;
import base.model.service.IEmployeeService;
import base.utils.IdComparator;
import base.utils.NameComparator;

@Controller
public class EmployeeController {

	@Autowired
	IEmployeeService iEmployeeService;

	// home page, show list of employees, page 1
	@GetMapping("/")
	public String showAll(Model model) {
		return findPaginated(1, model);
	}
	
	// pagination
	@GetMapping("/page/{pageNo}")
	public String findPaginated(@PathVariable(value="pageNo") int pageNo, Model model){
		int pageSize = 10;
		Page<Employee> page = iEmployeeService.findPaginated(pageNo, pageSize);
		List<Employee> employees = page.getContent();
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("employees", employees);
		return "index";
	}
	
	@GetMapping("/view/{id}")
	public String viewById(Model model, @PathVariable("id") long id) {
		Employee entity = new Employee();
		try {
			entity = iEmployeeService.getById(id);
			model.addAttribute("viewInform", "Employee is found");
		} catch (Exception exp) {
			model.addAttribute("viewInform", "Employee is not found for id = " + id);
		}
		model.addAttribute("employee", entity);
		return "viewEmployee";
	}

	// show the adding form to add new employee
	@GetMapping("/show-add-form")
	public String showAddForm(Model model) {
		Employee employee = new Employee();
		model.addAttribute("employee", employee);
		return "addForm";
	}

	// save new employee and update are similar
	@PostMapping("/add")
	public String addEmployee(@ModelAttribute("employee") Employee employee, RedirectAttributes redirectAttributes) {
		iEmployeeService.addEmployee(employee);
		redirectAttributes.addFlashAttribute("status", "add");
		return "redirect:/view/" + employee.getId();
	}

	// show update form for a specific id
	@GetMapping("/show-update-form/{id}")
	public String showUpdateForm(@PathVariable("id") long id, Model model) {
		try {
			Employee entity = iEmployeeService.getById(id);
			model.addAttribute("employee", entity);
			return "updateForm";
		} catch (Exception e) {
			return "redirect:/";
		}
	}
	
	@PostMapping("/update/{id}")
	public String update(RedirectAttributes redirectAttributes, @PathVariable long id, @ModelAttribute("employee") Employee employee) {
		// return null if failed
		Employee entity = iEmployeeService.updateById(id, employee);
		redirectAttributes.addFlashAttribute("status", "update");
		return "redirect:/view/" + entity.getId(); // return updated employee
	}

	// html does not support put and delete, so POST data (id) from form to handle in backend
	@PostMapping("/delete/{id}")
	public String deleteById(RedirectAttributes redirectAttributes, @PathVariable("id") long id) {
		try {
			iEmployeeService.deleteById(id);
			redirectAttributes.addFlashAttribute("delInform", "Delete employee with id = " + id + " successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("delInform", "Employee is not found with id = " + id);
		}
		return "redirect:/";
	}
	
	@GetMapping("/search")
	public String searchByName(@RequestParam("name-contain") String str, Model model) {
		List<Employee> employees = iEmployeeService.searchByName(str);		
		if(employees.size() == 0) {
			model.addAttribute("searchInform", "Not found any employee!");
		}
		else {
			employees.sort(new NameComparator());
			model.addAttribute("employees", employees);
		}
		return "index";
	}
}
