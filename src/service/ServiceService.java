package service;

import dao.ServiceDAO;
import model.Service;
import util.Validator;

import java.util.List;

public class ServiceService {

    private final ServiceDAO serviceDAO = new ServiceDAO();

    public boolean addService(String name, double price) {
        validateServiceInput(name, price);

        if (serviceDAO.existsByName(name.trim())) {
            throw new IllegalArgumentException("Tên dịch vụ đã tồn tại");
        }

        Service service = new Service();
        service.setName(name.trim());
        service.setPrice(price);
        return serviceDAO.insert(service);
    }

    public List<Service> getAllServices() {
        return serviceDAO.findAll();
    }

    public Service getServiceById(int id) {
        return serviceDAO.findById(id);
    }

    public boolean updateService(int id, String name, double price) {
        validateServiceInput(name, price);

        if (serviceDAO.findById(id) == null) {
            throw new IllegalArgumentException("Dịch vụ không tồn tại");
        }

        if (serviceDAO.existsByNameExceptId(name.trim(), id)) {
            throw new IllegalArgumentException("Tên dịch vụ đã tồn tại");
        }

        Service service = new Service();
        service.setId(id);
        service.setName(name.trim());
        service.setPrice(price);
        return serviceDAO.update(service);
    }

    public boolean deleteService(int id) {
        if (serviceDAO.findById(id) == null) {
            throw new IllegalArgumentException("Dịch vụ không tồn tại");
        }
        return serviceDAO.delete(id);
    }

    private void validateServiceInput(String name, double price) {
        if (Validator.isBlank(name)) {
            throw new IllegalArgumentException("Tên dịch vụ không được để trống");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Giá dịch vụ phải lớn hơn 0");
        }
    }
}

