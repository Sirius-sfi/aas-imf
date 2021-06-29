import os
from aas.model.base import EntityType, Namespace
import rdflib
import datetime
from pathlib import Path  # Used for easier handling of auxiliary file's local path
import pyecma376_2  # The base library for Open Packaging Specifications. We will use the OPCCoreProperties class.
from aas import model
from aas.adapter import aasx
from rdflib.term import Identifier

imfNamespace = "https://sirius.org/imf/"
uppNamespace = "http://data.aibel.com/asset/equinor/AskjaUPP/"

# Create graph and populate with template instances:
graph = rdflib.Graph()
graph.parse("rdf/mel_tpl_instances.ttl", format="n3")


dir = "sparql/tpl/"
for queryfile in os.listdir(dir):
    if queryfile.endswith(".rq"):
        with open(dir + queryfile) as file:
            query = file.read()
            qres = graph.query(query)
            for row in qres:
                print(row)
                print("\n")
            file.close()  

#functionAspect = model.SubmodelElementCollectionOrdered(
#    id_short="FunctionAspect"
#)
#locationAspect = model.SubmodelElementCollectionOrdered(
#    id_short="LocationAspect"
#)
#productAspect = model.SubmodelElementCollectionOrdered(
#    id_short="ProductAspect"
#)


entity = model.Entity(
    id_short="Tmp",
    semantic_id=model.Reference(
        (model.Key(
            type_=model.KeyElements.GLOBAL_REFERENCE,
            local=False,
            value="http://example.com/sldfkjgsdflgkj",
            id_type=model.KeyType.IRI
        ))
    ),
    entity_type=EntityType.CO_MANAGED_ENTITY
)

tmp2 = model.Submodel(
    id_short="TmpCollection"
    ,identification=model.Identifier(uppNamespace + "TmpCollection", model.IdentifierType.IRI)
#    semantic_id=model.Identifier(imfNamespace + "TmpCollection", model.IdentifierType.IRI),
    ,semantic_id=model.Reference(
        (model.Key(
            type_=model.KeyElements.GLOBAL_REFERENCE,
            local=False,
            value="http://example.com/sernysdgsdf123",
            id_type=model.KeyType.IRI
        ))
    )
    ,submodel_element=(entity)
)

imfAspectObjects = model.SubmodelElementCollectionUnordered(
    id_short="ImfAspectObjects"
#    ,identification=model.Identifier(imfNamespace + "ImfAspectObjects", model.IdentifierType.IRI)
#    ,semantic_id=model.Identifier(imfNamespace + "ImfAspectObjects", model.IdentifierType.IRI)
    ,semantic_id=model.Reference(
        (model.Key(
            type_=model.KeyElements.GLOBAL_REFERENCE,
            local=False,
            value="http://example.com/abc123",
            id_type=model.KeyType.IRI
        ))
    )
    ,value=(tmp2)
)



aspectBreakdown = model.Submodel(
    id_short="AspectBreakdown",
    category="CONSTANT",
    identification=model.Identifier(uppNamespace + "AspectBreakdown", model.IdentifierType.IRI),
    semantic_id=model.Identifier(imfNamespace + "AspectBreakdown", model.IdentifierType.IRI),
    submodel_element=(imfAspectObjects)
)


asset = model.Asset(
    kind=model.AssetKind.INSTANCE,  # define that the Asset is of kind instance
    identification=model.Identifier(id_="http://equinor.com/KRA/MEL", id_type=model.IdentifierType.IRI),
    id_short="mainprocessingsystem",
    description={"en" : "This is the node provided by Equinor"}
)

aas = model.AssetAdministrationShell(
    id_short="MEL",
    identification=model.Identifier(uppNamespace + "MEL", model.IdentifierType.IRI),
    asset=model.AASReference.from_referable(asset),
    submodel={model.AASReference.from_referable(aspectBreakdown)}
)

object_store = model.DictObjectStore([aspectBreakdown, asset, aas])
file_store = aasx.DictSupplementaryFileContainer()

with aasx.AASXWriter("aas/mel.aasx") as writer:
    writer.write_aas(aas_id=model.Identifier(uppNamespace + "MEL", model.IdentifierType.IRI),
                     object_store=object_store,
                     file_store=file_store,
                     submodel_split_parts=False)  # for compatibility with AASX Package Explorer
    writer.close
