import {Countries} from "../../../../models/country/country";

export interface Region {
  code: string;
  name: string;
}

export var regions: Region[] = [
  { code: "TN-11", name: "Tunis" },
  { code: "TN-12", name: "L'Ariana" },
  { code: "TN-13", name: "Ben Arous" },
  { code: "TN-14", name: "La Manouba" },
  { code: "TN-21", name: "Nabeul" },
  { code: "TN-22", name: "Zaghouan" },
  { code: "TN-23", name: "Bizerte" },
  { code: "TN-31", name: "Béja" },
  { code: "TN-32", name: "Jendouba" },
  { code: "TN-33", name: "Le Kef" },
  { code: "TN-34", name: "Siliana" },
  { code: "TN-41", name: "Kairouan" },
  { code: "TN-42", name: "Kasserine" },
  { code: "TN-43", name: "Sidi Bouzid" },
  { code: "TN-51", name: "Sousse" },
  { code: "TN-52", name: "Monastir" },
  { code: "TN-53", name: "Mahdia" },
  { code: "TN-61", name: "Sfax" },
  { code: "TN-71", name: "Gafsa" },
  { code: "TN-72", name: "Tozeur" },
  { code: "TN-73", name: "Kébili" },
  { code: "TN-81", name: "Gabès" },
  { code: "TN-82", name: "Médenine" },
  { code: "TN-83", name: "Tataouine" }
];
