package com.apapedia.user.restService;

import com.apapedia.user.repository.UserDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserRestServiceImpl implements UserRestService{
    @Autowired
    private UserDb userDb;
}
