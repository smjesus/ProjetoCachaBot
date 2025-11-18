/**
 * Projeto:  CachaBot  -  BOT para o Discord para gerenciar Usuarios.
 * Gerente:  Sergio Murilo  -  smurilo at GMail
 * Data:     Manaus/AM  -  2024
 * Equipe:   Murilo, Victor, Allan
 */
package br.com.aeroceti.cachaBot.componentes;

import br.com.aeroceti.cachaBot.entidades.NivelAcesso;
import br.com.aeroceti.cachaBot.repositorios.PermissoesRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Componente para criar automaticamente um objeto NivelAcesso
 * 
 * Esta classe intercepta um POST de cadastro/atualizacao de um Colaborador e
 * converte a String que representa um Nivel de Acesso em um objeto e injeta no Colaborador.
 * 
 * Rotina criada pelo ChatGPT.
 * 
 * @author smurilo
 */
@Component
public class StringToNivelAcessoConverter implements Converter<String, NivelAcesso> {

    private final PermissoesRepository nivelAcessoRepository;

    public StringToNivelAcessoConverter(PermissoesRepository nar) {
        this.nivelAcessoRepository = nar;
    }

    @Override
    public NivelAcesso convert(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return nivelAcessoRepository.findById(Long.valueOf(id)).orElse(null);
    }
}
/*                    End of Class                                            */