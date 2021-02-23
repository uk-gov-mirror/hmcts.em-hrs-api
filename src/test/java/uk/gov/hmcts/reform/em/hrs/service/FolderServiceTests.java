package uk.gov.hmcts.reform.em.hrs.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.em.hrs.componenttests.TestUtil;
import uk.gov.hmcts.reform.em.hrs.domain.Folder;
import uk.gov.hmcts.reform.em.hrs.repository.FolderRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FolderServiceTests {

    @Mock
    FolderRepository folderRepository;

    @InjectMocks
    FolderService folderService;

    @Test
    public void testFindOne() {

        when(this.folderRepository.findById(TestUtil.RANDOM_UUID)).thenReturn(Optional.of(TestUtil.folder));

        Optional<Folder> folder = folderService.findById(TestUtil.RANDOM_UUID);

        Assertions.assertEquals(Optional.of(TestUtil.folder), folder);

    }

    @Test
    public void testSave() {

        Folder folder = new Folder();

        folderService.save(folder);

        verify(folderRepository, times(1)).save(folder);

    }


    @Test
    public void testDelete() {

        when(this.folderRepository.findById(TestUtil.RANDOM_UUID)).thenReturn(Optional.of(TestUtil.folder));

        folderService.delete(TestUtil.RANDOM_UUID);

        verify(folderRepository, times(1)).delete(TestUtil.folder);

    }

}
