package badasintended.stages.api.init;

/**
 * Convenient main side entrypoint.<br>
 * The JSON entry id is {@code stages:main}
 */
@FunctionalInterface
public interface StagesInit {

    void onStagesInit();

}
