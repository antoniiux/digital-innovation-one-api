package br.com.beerestoque.service;

import lombok.AllArgsConstructor;
import br.com.beerestoque.dto.BeerDTO;
import br.com.beerestoque.entity.Beer;
import br.com.beerestoque.exception.BeerAlreadyRegisteredException;
import br.com.beerestoque.exception.BeerNotFoundException;
import br.com.beerestoque.mapper.BeerMapper;
import br.com.beerestoque.repository.BeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper = BeerMapper.INSTANCE;

    public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(beerDTO.getName());
        Beer beer = beerMapper.toModel(beerDTO);
        Beer savedBeer = beerRepository.save(beer);
        return beerMapper.toDTO(savedBeer);
    }

    public BeerDTO findByName(String name) throws BeerNotFoundException {
        Beer foundBeer = beerRepository.findByName(name)
                .orElseThrow(() -> new BeerNotFoundException(name));
        return beerMapper.toDTO(foundBeer);
    }

    public List<BeerDTO> listAll() {
        return beerRepository.findAll()
                .stream()
                .map(beerMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws BeerNotFoundException {
        verifyIfExists(id);
        beerRepository.deleteById(id);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws BeerAlreadyRegisteredException {
        Optional<Beer> optSavedBeer = beerRepository.findByName(name);
        if (optSavedBeer.isPresent()) {
            throw new BeerAlreadyRegisteredException(name);
        }
    }

    private Beer verifyIfExists(Long id) throws BeerNotFoundException {
        return beerRepository.findById(id)
                .orElseThrow(() -> new BeerNotFoundException(id));
    }

    public BeerDTO increment(Long id, int quantityToIncrement) throws BeerNotFoundException, br.com.beerestoque.exception.BeerEstoqueExceededException {
        Beer beerToIncrementEstoque = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + beerToIncrementEstoque.getQuantity();
        if (quantityAfterIncrement <= beerToIncrementEstoque.getMax()) {
            beerToIncrementEstoque.setQuantity(beerToIncrementEstoque.getQuantity() + quantityToIncrement);
            Beer incrementedBeerEstoque = beerRepository.save(beerToIncrementEstoque);
            return beerMapper.toDTO(incrementedBeerEstoque);
        }
        throw new br.com.beerestoque.exception.BeerEstoqueExceededException(id, quantityToIncrement);
    }
}
