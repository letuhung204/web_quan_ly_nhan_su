package base.model.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import base.model.Employee;
import base.repo.IEmployeeRepo;

@Service
public class EmployeeService implements IEmployeeService {
	@Autowired
	private IEmployeeRepo iEmployeeRepo;

	@Override
	public List<Employee> getAll() {
		return iEmployeeRepo.findAll();
	}

	@Override
	public Employee addEmployee(Employee e) {
		iEmployeeRepo.save(e);
		return e;
	}

	@Override
	public void deleteById(long id) {
		Optional<Employee> optional = iEmployeeRepo.findById(id);
		if (optional.isPresent()) {
			iEmployeeRepo.deleteById(id);
		} else {
			throw new RuntimeException("Not found anything by id = " + id);
		}
	}

	@Override
	public Employee getById(long id) {
		Optional<Employee> optional = iEmployeeRepo.findById(id);
		Employee e = null;
		if (optional.isPresent()) {
			e = optional.get();
			return e;
		} else {
			throw new RuntimeException("Not found anything by id = " + id);
		}
	}

	@Override
	public Employee updateById(Long id, Employee e) {
		try {
			Employee entity = getById(id);
			entity.setFirstName(e.getFirstName());
			entity.setLastName(e.getLastName());
			entity.setPhone(e.getPhone());
			entity.setDepartment(e.getDepartment());
			entity.setEmail(e.getEmail());
			entity.setPassword(e.getPassword());
			iEmployeeRepo.save(entity);
			return entity;
		} catch (Exception exc) {
			System.out.println("Not found employee with id = " + id);
			return null;
		}
	}

	@Override
	public List<Employee> searchByName(String str) {
		return iEmployeeRepo.searchByName(str);
	}
	
	@Override
	public Page<Employee> findPaginated(int pageNo, int pageSize){
		Pageable pageable = PageRequest.of(pageNo-1, pageSize, Sort.by("id").ascending());
		return iEmployeeRepo.findAll(pageable);
	}
}
