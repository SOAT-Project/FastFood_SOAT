package soat.project.fastfoodsoat.domain.client;

import java.util.Optional;

public interface ClientGateway {
    Optional<Client> findByCpf(ClientCpf cpf);
    Client create(Client client);
    Optional<Client> findById(ClientId id);
}
