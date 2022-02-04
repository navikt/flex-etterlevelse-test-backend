package no.nav.helse.flex.juridiskvurdering

import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class JuridiskVurderingListener(
    private val juridiskVurderingRepository: JuridiskVurderingRepository,
) {

    val log = logger()

    @KafkaListener(
        topics = ["flex.omrade-helse-etterlevelse"],
        idIsGroup = false,
        containerFactory = "aivenKafkaListenerContainerFactory",
    )
    fun listen(cr: ConsumerRecord<String, String>, acknowledgment: Acknowledgment) {

        data class FelterFraVurdering(val paragraf: String, val utfall: String)

        val fnr = cr.key()
        val juridiskVurderingJson = cr.value()
        if (fnr.erFnr()) {

            val deserialisert: FelterFraVurdering = objectMapper.readValue(juridiskVurderingJson)

            juridiskVurderingRepository.save(
                JuridiskVurderingDbRecord(
                    id = null,
                    fnr = fnr,
                    opprettet = OffsetDateTime.now(),
                    juridiskVurdering = juridiskVurderingJson,
                    paragraf = deserialisert.paragraf,
                    utfall = deserialisert.utfall,
                )
            )
        } else {
            log.error("Key på kafka er ikke : $fnr , hele meldingen: $juridiskVurderingJson")
        }

        acknowledgment.acknowledge()
    }
}
