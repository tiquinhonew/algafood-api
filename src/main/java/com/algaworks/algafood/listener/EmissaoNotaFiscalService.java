package com.algaworks.algafood.listener;

import com.algaworks.algafood.di.service.ClienteAtivadoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmissaoNotaFiscalService {

    @EventListener
    public void clienteAtivador (ClienteAtivadoEvent event) {
        System.out.println("Emitindo nota fiscal para cliente " + event.getCliente().getNome());
    }
}
