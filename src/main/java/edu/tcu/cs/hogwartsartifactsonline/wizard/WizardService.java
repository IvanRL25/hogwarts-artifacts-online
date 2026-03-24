package edu.tcu.cs.hogwartsartifactsonline.wizard;

import edu.tcu.cs.hogwartsartifactsonline.artifact.Artifact;
import edu.tcu.cs.hogwartsartifactsonline.artifact.ArtifactRepository;
import edu.tcu.cs.hogwartsartifactsonline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

    private final WizardRepository wizardRepository;

    private final ArtifactRepository artifactRepository;

    public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {
        this.wizardRepository = wizardRepository;
        this.artifactRepository = artifactRepository;
    }

    public List<Wizard> findAll(){
        return this.wizardRepository.findAll();
    }

    public Wizard findById(Integer wizardId){
        return this.wizardRepository.findById(wizardId)
                .orElseThrow(()-> new ObjectNotFoundException("wizard",wizardId));
    }

    public Wizard save(Wizard newWizard){
        return this.wizardRepository.save(newWizard);
    }

    public Wizard update(Integer wizardId, Wizard update){
        return this.wizardRepository.findById(wizardId)
                .map(oldwizard ->{
                    oldwizard.setName(update.getName());
                    return this.wizardRepository.save(oldwizard);
                })
                .orElseThrow(()-> new ObjectNotFoundException("wizard",wizardId));
    }

    public void delete(Integer wizardId){
        Wizard wizardToDelete = this.wizardRepository.findById(wizardId)
                .orElseThrow(()-> new ObjectNotFoundException("wizard",wizardId));

        wizardToDelete.removeAllArtifacts();
        this.wizardRepository.deleteById(wizardId);
    }

    public void assignArtifact(Integer wizardId, String artifactId){
        //find this artifact by Id from the DB.

        Artifact artifactToBeAssigned = this.artifactRepository.findById(artifactId)
                .orElseThrow( ()-> new ObjectNotFoundException("artifact",artifactId));

        //find this wizard by Id from the Db.

        Wizard wizard = this.wizardRepository.findById(wizardId)
                .orElseThrow( ()-> new ObjectNotFoundException("wizard",wizardId));

        //Artifact Assignment
        //We need to see if the artifact is already owned by some wizard.
        if(artifactToBeAssigned.getOwner() != null){
            artifactToBeAssigned.getOwner().removeArtifact(artifactToBeAssigned);
        }
        wizard.addArtifact(artifactToBeAssigned);

    }
}
