package com.example.banking.service.business;

import org.springframework.stereotype.Service;

import com.example.banking.repository.CustomerMongoRepository;
import com.example.banking.service.TransferService;
import com.example.banking.service.business.exception.AccountNotFoundException;
import com.example.banking.service.business.exception.CustomerNotFoundException;

@Service
public class StandardTransferService implements TransferService {
	private CustomerMongoRepository customerMongoRepository;

	public StandardTransferService(CustomerMongoRepository customerMongoRepository) {
		this.customerMongoRepository = customerMongoRepository;
	}

	@Override
	public void transfer(String fromIdentity, String fromIban, String toIdentity, String toIban, double amount) {
		//check is customer valid
		var newCustomer = customerMongoRepository.findById(fromIdentity)
												 .orElseThrow(()-> 
												 new CustomerNotFoundException("customer is not valid", fromIdentity)) ;
		//check is account valid
		//after withdraw amount
		newCustomer.getAccounts()
				   .stream()
			       .filter(a->a.getIban().equals(fromIban))
			       .findFirst()
			       .orElseThrow(()-> 
			       new AccountNotFoundException("account is not valid", fromIban))
			       .withdraw(amount);
		
		//save new changes on database
		customerMongoRepository.save(newCustomer);
		
		//check is customer valid
		newCustomer = customerMongoRepository.findById(toIdentity)
											 .orElseThrow(()-> 
											 new CustomerNotFoundException("customer is not valid", fromIdentity)) ;
		//check is account valid
		//after withdraw amount
		newCustomer.getAccounts()
				   .stream()
				   .filter(a -> a.getIban().equals(toIban))
				   .findFirst()
				   .orElseThrow(()-> 
			       new AccountNotFoundException("account is not valid", fromIban))
				  .deposit(amount);	
		//save new changes on database
		customerMongoRepository.save(newCustomer);

	}

}
