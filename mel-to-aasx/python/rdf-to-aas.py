import argparse
import sys
import os
from aas.model.base import EntityType, Namespace
import datetime
from pathlib import Path  # Used for easier handling of auxiliary file's local path
import pyecma376_2  # The base library for Open Packaging Specifications. We will use the OPCCoreProperties class.
from aas import model
from aas.adapter import aasx
from rdflib.term import Identifier

ap = argparse.ArgumentParser(
    description='Package MEL payload RDF as AASX.', 
    add_help=True
)

ap.add_argument("-aau", "--aas_uri", metavar="<uri_id>", required=True, help="The URI ID of the AAS.")
ap.add_argument("-asu", "--asset_uri", metavar="<uri_id>", required=True, help="The URI ID of the AAS Asset.")
ap.add_argument("-smu", "--submodel_uri", metavar="<uri_id>", required=True, help="The URI ID of the AAS Submodel containing the RDF payload.")
ap.add_argument("-aas", "--aas_id_short", metavar="<id_short>", required=False, help="The id_short of the AAS.")
ap.add_argument("-ass", "--asset_id_short", metavar="<id_short>", required=False, help="The id_short of the AAS Asset.")
ap.add_argument("-sms", "--submodel_id_short", metavar="<id_short>", required=False, help="The id_short of the AAS Submodel containing the RDF payload.")
ap.add_argument("-p", "--rdf_payload", metavar="<turtle_rdf_file>", required=True, help="The RDF payload file in Turtle syntax.")
ap.add_argument("-o", "--output", metavar="<aasx_file>", required=True, help="The output AASX file.")

args = vars(ap.parse_args())

#imfNamespace = "https://sirius.org/imf/"
#uppNamespace = "http://data.aibel.com/asset/equinor/AskjaUPP/"

print(args)

submodel = model.Submodel(
    id_short=args['submodel_id_short'],
    identification=model.Identifier(args['submodel_uri'], model.IdentifierType.IRI)
)
asset = model.Asset(
    id_short=args['asset_id_short'],
    identification=model.Identifier(id_=args['asset_uri'], id_type=model.IdentifierType.IRI),
    kind=model.AssetKind.INSTANCE,  # define that the Asset is of kind instance
    description={"en" : "This is the node provided by Equinor"}
)
aas = model.AssetAdministrationShell(
    id_short=args['aas_id_short'],
    identification=model.Identifier(args['aas_uri'], model.IdentifierType.IRI),
    asset=model.AASReference.from_referable(asset),
    submodel={model.AASReference.from_referable(submodel)}
)

object_store = model.DictObjectStore([submodel, asset, aas])
file_store = aasx.DictSupplementaryFileContainer()

with open(args['rdf_payload'], 'rb') as f:
    actual_file_name = file_store.add_file("/aasx/suppl/mel_rdf.ttl", f, "text/turtle")

submodel.submodel_element.add(
    model.File(id_short="mel_rdf_payload",
               mime_type="text/turtle",
               value=actual_file_name))


with aasx.AASXWriter(args['output']) as writer:
    writer.write_aas(aas_id=model.Identifier(args['aas_uri'], model.IdentifierType.IRI),
                     object_store=object_store,
                     file_store=file_store,
                     submodel_split_parts=False)  # for compatibility with AASX Package Explorer
    writer.close
