package ua.com.alevel.service.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.alevel.model.phone.View;
import ua.com.alevel.repository.phone.PhoneRepository;
import ua.com.alevel.repository.view.ViewRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ViewService {
    private final ViewRepository viewRepository;
    private final PhoneRepository phoneRepository;

    @Autowired
    public ViewService(ViewRepository viewRepository, PhoneRepository phoneRepository) {
        this.viewRepository = viewRepository;
        this.phoneRepository = phoneRepository;
    }

    public List<View> findAllViews() {
        List<View> views = new ArrayList<>();
        viewRepository.findAll().forEach(view -> views.add(view));
        Collections.sort(views);

        return views;
    }

    public View findById(String id) {
        return viewRepository.findById(id).get();
    }

    public boolean delete(String id) {
        if (phoneRepository.findFirstByView(viewRepository.findById(id).get()).isPresent()) {
            return false;
        }
        else {
            viewRepository.deleteById(id);
            return true;
        }
    }

    public boolean save(View view) {
        if (!viewRepository.findFirstByColorPhoneFrontAndBackLeftSideAndRightSideUpSideAndDownSide(view.getColor(), view.getPhoneFrontAndBack(),
                view.getLeftSideAndRightSide(), view.getUpSideAndDownSide()).isEmpty()) {
            return false;
        }
        else {
            viewRepository.save(view);
            return true;
        }
    }

    public void update(View view) {
        viewRepository.save(view);
    }
}

