package hu.sourcescode.teleportscroll.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;


@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Scroll {

    private String name;
    private Location destinationLocation;

}
